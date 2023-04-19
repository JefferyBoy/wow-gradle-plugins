package com.github.jeffery.aspectj

class AspectFileManager {

    private String tmpDir
    private Collection<File> closureFiles
    private File closureFile

    AspectFileManager(String dir, String fileName) {
        this.tmpDir = tmpDir
        closureFile = new File(dir, fileName)
        closureFiles = new HashSet<>()
        getFile()
    }

    Collection<File> getFile() {
        if (closureFiles.empty && closureFile.exists()) {
            closureFile.readLines().forEach {
                def f = new File(it)
                if (f.exists() && f.isFile()) {
                    closureFiles.add(f)
                }
            }
        }
        return closureFiles
    }

    void addFile(Collection<File> files) {
        addFileInternal(files, true)
    }

    private void addFileInternal(Collection<File> files, boolean append) {
        if (files != null) {
            def out = new FileOutputStream(closureFile, append)
            files.forEach {
                if (it.exists() && it.isFile()) {
                    if (closureFiles.add(it)) {
                        out.write("${it.absolutePath}\n".getBytes())
                    }
                }
            }
            out.flush()
            out.close()
        }
    }

    void removeFile(Collection<File> files) {
        if (files != null && !files.isEmpty()) {
            files.forEach {
                if (it.exists() && it.isFile()) {
                    if (it.delete()) {
                        closureFiles.remove(it)
                    }
                }
            }
            addFileInternal(closureFiles, false)
        }
    }

    void clearFile() {
        closureFiles.forEach {
            it.delete()
        }
        closureFiles.clear()
        if (closureFile.exists()) {
            closureFile.delete()
        }
    }
}