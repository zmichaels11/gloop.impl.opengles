/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles2x;

import com.longlinkislong.gloop.glspi.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
final class GLES2XBuffer implements Buffer {
    int bufferId = -1;
    ByteBuffer mapBuffer;
    long size;
    int usage;

    @Override
    public boolean isValid() {
        return bufferId != -1;
    }
    
}
