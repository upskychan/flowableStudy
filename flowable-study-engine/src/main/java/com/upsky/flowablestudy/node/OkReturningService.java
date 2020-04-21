package com.upsky.flowablestudy.node;

import java.io.Serializable;

public class OkReturningService implements Serializable {

    public String invoke() {
        return "分享牛1";
    }
}
