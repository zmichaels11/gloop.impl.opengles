/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles2x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.DriverProvider;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengles.GLES;

/**
 *
 * @author zmichaels
 */
public final class GLES2XDriverProvider implements DriverProvider {
    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("opengles", "angle");
    }

    private static final class Holder {
        private Holder() {}
        private static final GLES2XDriver INSTANCE = new GLES2XDriver();
    }

    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean is64bitUniformsSupported() {
        return false;
    }

    @Override
    public boolean isBufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isComputeShaderSupported() {
        return false;
    }

    @Override
    public boolean isDrawIndirectSupported() {
        return false;
    }

    @Override
    public boolean isDrawInstancedSupported() {
        return true;
    }

    @Override
    public boolean isDrawQuerySupported() {
        return false;
    }

    @Override
    public boolean isFramebufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isImmutableBufferStorageSupported() {
        return false;
    }

    @Override
    public boolean isInvalidateSubdataSupported() {
        return false;
    }

    @Override
    public boolean isProgramSupported() {
        return true;
    }

    @Override
    public boolean isSamplerSupported() {
        return false;
    }

    @Override
    public boolean isSeparateShaderObjectsSupported() {
        return false;
    }

    @Override
    public boolean isSparseTextureSupported() {
        return false;
    }

    @Override
    public boolean isSupported() {
        return GLES.getCapabilities().GLES20;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }
    
}
