package com.github.jeffery.aspectj

class AspectjCache {
    def aspectClasses = new HashSet<String>()
    def aspectJoinPoints = new HashMap<String, Collection<String>>()

    void clear() {
        aspectClasses.clear()
        aspectJoinPoints.clear()
    }

    void addAspect(Object aspect) {
        if (aspect != null) {
            if (aspect instanceof Collection) {
                aspectClasses.addAll(aspect.collect { it.toString() })
            } else {
                aspectClasses.add(aspect.toString())
            }
        }
    }

    void removeAspect(Object aspect) {
        if (aspect != null) {
            if (aspect instanceof Collection) {
                aspectClasses.removeAll(aspect.collect { it.toString() })
            } else {
                aspectClasses.remove(aspect.toString())
            }
        }
    }

    void addJoinPoint(Object pointCutFile, Collection<Object> pointCutClosureFile) {
        if (pointCutFile != null && pointCutClosureFile != null) {
            aspectJoinPoints.put(pointCutFile.toString(), pointCutClosureFile)
        }
    }

    void removeJoinPoint(Object pointCutFile) {
        if (pointCutFile != null) {
            def path = pointCutFile.toString()
            def v = aspectJoinPoints.remove(path)
            if (v != null) {
                if (path.length() > 0) {
                    new File(path).delete()
                }
                v.forEach {
                    if (it != null && it.length() > 0) {
                        new File(it).delete()
                    }
                }
            }
        }
    }
}