package com.bkbits.exception;

import java.util.IdentityHashMap;

enum VisitState {
    VISITING,
    VISITED,
}

class CycleDetector<T> {
    private final IdentityHashMap<T, VisitState> visitMap = new IdentityHashMap<>();
    private boolean hasCycle = false;
    private int depth = 0;

    public boolean visitBegin(T node) {
        if (this.hasCycle) { // 已经检测出循环引用，直接返回
            return true;
        }
        VisitState visitState = visitMap.get(node);
        if (visitState == null) {
            ++depth;
            visitMap.put(node, VisitState.VISITING);
            return false;
        }

        if (visitState == VisitState.VISITED) {
            return false;
        }

        ++depth;
        hasCycle = true;
        return true;
    }

    public void visitEnd(T node) {
        if (this.hasCycle) { // 已经检测出循环引用，直接返回
            return;
        }

        --depth;
        visitMap.put(node, VisitState.VISITED);
    }

    public int getDepth() {
        return depth;
    }
}
