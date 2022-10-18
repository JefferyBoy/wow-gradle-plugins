package com.github.jeffery.aspectj;

import java.util.List;

/**
 * @author mxlei
 * @date 2022/9/23
 */
public class AspectjExtension {
    private List<String> aspectExclude;
    private List<String> aspectInclude;

    public List<String> getAspectExclude() {
        return aspectExclude;
    }

    public void setAspectExclude(List<String> aspectExclude) {
        this.aspectExclude = aspectExclude;
    }

    public List<String> getAspectInclude() {
        return aspectInclude;
    }

    public void setAspectInclude(List<String> aspectInclude) {
        this.aspectInclude = aspectInclude;
    }
}
