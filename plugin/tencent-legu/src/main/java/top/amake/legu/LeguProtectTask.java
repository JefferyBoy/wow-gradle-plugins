package top.amake.legu;


import com.android.builder.model.SigningConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.DeleteObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.utils.StringUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ms.v20180408.MsClient;
import com.tencentcloudapi.ms.v20180408.models.AppInfo;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceRequest;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceResponse;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultRequest;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultResponse;
import com.tencentcloudapi.ms.v20180408.models.ServiceInfo;

import org.apache.commons.codec.binary.Hex;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

import javax.inject.Inject;

import jdk.security.jarsigner.JarSigner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author mxlei
 * @date 2022/9/21
 */
public class LeguProtectTask extends DefaultTask {

    private String apkPath;
    private LeguExtension config;
    private SigningConfig signConfig;
    private final Logger log = Logging.getLogger(getClass());

    @Inject
    public LeguProtectTask(String apkPath, LeguExtension config, SigningConfig signConfig) {
        this.apkPath = apkPath;
        this.config = config;
        this.signConfig = signConfig;
    }

    @TaskAction
    public void execute() {
        //需要加固的apk
        File apkFile = new File(apkPath);
        //加固后的apk
        File apkFileOut = new File(apkFile.getParent(), apkFile.getName().replace(".apk", "-protected.apk"));

        try {
            if (!apkFile.exists() || !apkFile.getName().endsWith(".apk")) {
                log.error("The apk is not exist");
                return;
            }
            //检查配置项
            if (!checkRequireConfiguration(config)) {
                return;
            }
            //上传apk
            String cosUrl = uploadObjectToCos(
                apkPath,
                config.getTencentCloudCosBucket(),
                config.getTencentCloudCosRegion(),
                apkFile.getName()
            );
            //创建加固任务
            Credential cred = new Credential(config.getTencentCloudSecretId(), config.getTencentCloudSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("ms.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            MsClient client = new MsClient(cred, "", clientProfile);
            CreateShieldInstanceRequest req = new CreateShieldInstanceRequest();
            AppInfo appInfo = new AppInfo();
            appInfo.setAppMd5(md5(apkPath));
            appInfo.setAppUrl(cosUrl);
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setServiceEdition("basic");
            serviceInfo.setSubmitSource("RDM-rdm");
            serviceInfo.setCallbackUrl("");
            req.setAppInfo(appInfo);
            req.setServiceInfo(serviceInfo);
            log.quiet("Create apk protect task");
            CreateShieldInstanceResponse resp = client.CreateShieldInstance(req);
            String itemId = resp.getItemId();
            log.quiet("Processing protect apk, this will take several minutes.");
            //等待加固完成
            String params = String.format("{\"ItemId\":\"%s\"}", itemId);
            DescribeShieldResultRequest resultReq = DescribeShieldResultRequest.fromJsonString(
                params,
                DescribeShieldResultRequest.class
            );
            DescribeShieldResultResponse resultResp;
            int checkCount = 0;
            do {
                TimeUnit.SECONDS.sleep(5);
                checkCount++;
                resultResp = client.DescribeShieldResult(resultReq);
                log.quiet("please wait {} seconds", checkCount * 5);
            } while (resultResp.getTaskStatus() == 2L);
            if (resultResp.getTaskStatus() == 1L) {
                //加固成功
                log.quiet("Protect success");
                boolean ok = download(resultResp.getShieldInfo().getAppUrl(), apkFileOut.getAbsolutePath());
                if (ok && apkFileOut.exists()) {
                    log.quiet("Protect success");
                    log.quiet(apkFileOut.getAbsolutePath());
                    String signedApk = signApk(apkFileOut.getAbsolutePath(), signConfig);
                    if (!StringUtils.isNullOrEmpty(signedApk) && new File(signedApk).exists()) {
                        log.quiet("Apk Signature success");
                        log.quiet(signedApk);
//                        apkFileOut.delete()
//                        File(signedApk).renameTo(apkFileOut)
                    }
                } else {
                    log.error("Download file failed");
                }
            } else {
                //加固失败
                log.error("Protect fail {}", resultResp.getStatusDesc());
            }
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //删除上传的APK
            if (config.isTencentCloudCosDeleteFileAfterTask()) {
                deleteObjectFromCos(
                    config.getTencentCloudCosBucket(),
                    config.getTencentCloudCosRegion(),
                    apkFile.getName()
                );
            }
        }

    }

    /**
     * 检查配置是否正确
     */
    private boolean checkRequireConfiguration(LeguExtension model) {
        if (StringUtils.isNullOrEmpty(model.getTencentCloudSecretId())) {
            log.error("Please config secretId");
            return false;
        }
        if (StringUtils.isNullOrEmpty(model.getTencentCloudSecretKey())) {
            log.error("Please config secretKey");
            return false;
        }
        if (StringUtils.isNullOrEmpty(model.getTencentCloudCosBucket())) {
            log.error("Please config cosBucket");
            return false;
        }
        if (StringUtils.isNullOrEmpty(model.getTencentCloudCosRegion())) {
            log.error("Please config cosRegion");
            return false;
        }
        return true;
    }

    /**
     * 上传文件到COS
     */
    private String uploadObjectToCos(
        String localFilePath,
        String bucket,
        String region,
        String key
    ) {
        log.quiet("Uploading file");
        File localFile = new File(localFilePath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, localFile);
        COSCredentials cred =
            new BasicCOSCredentials(config.getTencentCloudSecretId(), config.getTencentCloudSecretKey());
        ClientConfig cosConfig = new ClientConfig(new Region(region));
        COSClient cos = new COSClient(cred, cosConfig);
        String url = null;
        try {
            cos.putObject(putObjectRequest);
            url =
                "https://" + config.getTencentCloudCosBucket() + ".cos." + config.getTencentCloudCosRegion() + ".myqcloud.com/" + key;
            log.quiet("Upload success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Upload fail {}", e.getMessage());
        } finally {
            cos.shutdown();
        }
        return url;
    }

    /**
     * 从COS中删除文件
     */
    private boolean deleteObjectFromCos(
        String bucket,
        String region,
        String key
    ) {
        DeleteObjectRequest req = new DeleteObjectRequest(bucket, key);
        BasicCOSCredentials cred = new BasicCOSCredentials(config.getTencentCloudSecretId(), config.getTencentCloudSecretKey());
        ClientConfig cosConfig = new ClientConfig(new Region(region));
        COSClient cos = new COSClient(cred, cosConfig);
        try {
            cos.deleteObject(req);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cos.shutdown();
        }
        return false;
    }

    private String md5(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(filePath);
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff, 0, buff.length)) != -1) {
                digest.update(buff, 0, len);
            }
            byte[] md5 = digest.digest();
            return Hex.encodeHexString(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载加固后的文件
     */
    private boolean download(String url, String saveFile) {
        log.quiet("Download file");
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
        try {
            Response response = client.newCall(request).execute();
            BufferedOutputStream write = new BufferedOutputStream(new FileOutputStream(saveFile, false));
            BufferedInputStream read = new BufferedInputStream(
                response.body().byteStream()
            );
            long contentLength = response.body().contentLength();
            long downLength = 0;
            byte[] bytes = new byte[1024];
            int len;
            float progress = 0f;
            while ((len = read.read(bytes, 0, bytes.length)) != -1) {
                write.write(bytes, 0, len);
                downLength += len;
                float p = 100f * downLength / contentLength;
                if (p - progress >= 1) {
                    progress = p;
                    log.quiet(
                        "Download progress {}%",
                        String.format(Locale.getDefault(), "%.1f", p)
                    );
                }
            }
            read.close();
            write.close();
            log.quiet("Download complete");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Download fail {}", e.getMessage());
        }
        return false;
    }

    /**
     * APK签名
     *
     * @param filePath   apk源文件
     * @param signConfig 签名配置
     * @return 签名后的文件路径
     */
    private String signApk(String filePath, SigningConfig signConfig) {
        File file = new File(filePath);
        if (!file.exists()) {
            return "";
        }
        if (signConfig == null || signConfig.getStoreFile() == null || !signConfig.getStoreFile().exists()) {
            log.error("apk sign keystore file not exist");
            return "";
        }
        try {
            KeyStore ks =
                KeyStore.getInstance(signConfig.getStoreFile(), signConfig.getStorePassword().toCharArray());
            Key key = ks.getKey(signConfig.getKeyAlias(), signConfig.getKeyPassword().toCharArray());
            CertPath certPath = CertificateFactory.getInstance("X.509")
                .generateCertPath(Arrays.asList(ks.getCertificateChain(signConfig.getKeyAlias())));
            JarSigner signer = new JarSigner.Builder((PrivateKey) key, certPath)
                .digestAlgorithm("SHA-256")
                .signatureAlgorithm("SHA256withRSA")
                .build();
            ZipFile zipFile = new ZipFile(filePath);
            String outFilePath = file.getParentFile().getAbsolutePath()
                + File.separator
                + file.getName().substring(0, file.getName().length() - 4)
                + "-signed.apk";
            signer.sign(zipFile, new FileOutputStream(outFilePath));
            return outFilePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
