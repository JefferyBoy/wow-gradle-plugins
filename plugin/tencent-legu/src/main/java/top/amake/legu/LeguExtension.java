package top.amake.legu;

/**
 * @author mxlei
 * @date 2022/9/21
 */
public class LeguExtension {
    /**
     * 腾讯云secretId
     */
    private String tencentCloudSecretId = "";

    /**
     * 腾讯云secretKey
     */
    private String tencentCloudSecretKey = "";

    /**
     * 腾讯COS bucket名称
     */
    private String tencentCloudCosBucket = "";

    /**
     * 腾讯COS region区域名称
     */
    private String tencentCloudCosRegion = "";

    /**
     * 加固任务完成后自动删除cos中的文件
     */
    private boolean tencentCloudCosDeleteFileAfterTask = true;

    public String getTencentCloudSecretId() {
        return tencentCloudSecretId;
    }

    public void setTencentCloudSecretId(String tencentCloudSecretId) {
        this.tencentCloudSecretId = tencentCloudSecretId;
    }

    public String getTencentCloudSecretKey() {
        return tencentCloudSecretKey;
    }

    public void setTencentCloudSecretKey(String tencentCloudSecretKey) {
        this.tencentCloudSecretKey = tencentCloudSecretKey;
    }

    public String getTencentCloudCosBucket() {
        return tencentCloudCosBucket;
    }

    public void setTencentCloudCosBucket(String tencentCloudCosBucket) {
        this.tencentCloudCosBucket = tencentCloudCosBucket;
    }

    public String getTencentCloudCosRegion() {
        return tencentCloudCosRegion;
    }

    public void setTencentCloudCosRegion(String tencentCloudCosRegion) {
        this.tencentCloudCosRegion = tencentCloudCosRegion;
    }

    public boolean isTencentCloudCosDeleteFileAfterTask() {
        return tencentCloudCosDeleteFileAfterTask;
    }

    public void setTencentCloudCosDeleteFileAfterTask(boolean tencentCloudCosDeleteFileAfterTask) {
        this.tencentCloudCosDeleteFileAfterTask = tencentCloudCosDeleteFileAfterTask;
    }
}
