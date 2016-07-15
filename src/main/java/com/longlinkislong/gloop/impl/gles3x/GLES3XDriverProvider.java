/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.DriverProvider;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengles.GLES;

/**
 *
 * @author zmichaels
 */
public final class GLES3XDriverProvider implements DriverProvider {

    private static final class Holder {

        private static final GLES3XDriver INSTANCE = new GLES3XDriver();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Driver getDriverInstance() {
        return Holder.INSTANCE;
    }
    
    @Override
    public List<String> getDriverDescription() {
        return Arrays.asList("opengles");
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
        return GLES.getCapabilities().GLES31;
    }

    @Override
    public boolean isDrawIndirectSupported() {
        return GLES.getCapabilities().GLES31;
    }

    @Override
    public boolean isDrawInstancedSupported() {
        return true;
    }

    @Override
    public boolean isDrawQuerySupported() {
        return true;
    }

    @Override
    public boolean isFramebufferObjectSupported() {
        return true;
    }

    @Override
    public boolean isImmutableBufferStorageSupported() {
        return GLES.getCapabilities().GL_EXT_buffer_storage;
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
        return true;
    }

    @Override
    public boolean isSeparateShaderObjectsSupported() {
        return GLES.getCapabilities().GLES31;
    }

    @Override
    public boolean isSparseTextureSupported() {
        return false;
    }

    @Override
    public boolean isSupported() {
        return GLES.getCapabilities().GLES30;
    }

    @Override
    public boolean isVertexArrayObjectSupported() {
        return true;
    }

}
