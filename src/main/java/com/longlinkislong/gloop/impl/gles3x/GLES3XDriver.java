/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import com.longlinkislong.gloop.impl.gles.CommonUtils;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengles.EXTBufferStorage;
import org.lwjgl.opengles.EXTTextureFilterAnisotropic;
import static org.lwjgl.opengles.EXTTextureFormatBGRA8888.GL_BGRA_EXT;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES30;
import org.lwjgl.opengles.GLES31;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public final class GLES3XDriver implements Driver<
        GLES3XBuffer, GLES3XFramebuffer, GLES3XRenderbuffer, GLES3XTexture, GLES3XShader, GLES3XProgram, GLES3XSampler, GLES3XVertexArray> {

    @Override
    public void blendingDisable() {

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void blendingEnable(int rgbEq, int aEq, int rgbFuncSrc, int rgbFuncDst, int aFuncSrc, int aFuncDst) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(rgbFuncSrc, rgbFuncDst, aFuncSrc, aFuncDst);
        GLES20.glBlendEquationSeparate(rgbEq, aEq);
    }

    @Override
    public void bufferAllocate(GLES3XBuffer buffer, long size, int usage) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, usage);
        buffer.size = size;
        buffer.usage = usage;
    }

    @Override
    public void bufferAllocateImmutable(GLES3XBuffer buffer, long size, int bitflags) {
        if (GLES.getCapabilities().GL_EXT_buffer_storage) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
            EXTBufferStorage.glBufferStorageEXT(GLES20.GL_ARRAY_BUFFER, size, bitflags);
            buffer.access = bitflags;
        } else {
            bufferAllocate(buffer, size, GLES20.GL_DYNAMIC_DRAW);
        }
    }

    @Override
    public void bufferBindAtomic(GLES3XBuffer bt, int i) {
        throw new UnsupportedOperationException("Atomic buffers are not supported in OpenGLES 3.0!");
    }

    @Override
    public void bufferBindAtomic(GLES3XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Atomic buffers are not supported in OpenGLES 3.0!");
    }

    @Override
    public void bufferBindFeedback(GLES3XBuffer bt, int index) {
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK, index, bt.bufferId);
    }

    @Override
    public void bufferBindFeedback(GLES3XBuffer bt, int index, long offset, long size) {
        GLES30.glBindBufferRange(GLES30.GL_TRANSFORM_FEEDBACK, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferBindStorage(GLES3XBuffer bt, int index) {
        if (GLES.getCapabilities().GLES31) {
            GLES30.glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, index, bt.bufferId);
        } else {
            throw new UnsupportedOperationException("OpenGLES 3.1 is not supported!");
        }
    }

    @Override
    public void bufferBindStorage(GLES3XBuffer bt, int index, long offset, long size) {
        if (GLES.getCapabilities().GLES31) {
            GLES30.glBindBufferRange(GLES31.GL_SHADER_STORAGE_BUFFER, index, bt.bufferId, offset, size);
        } else {
            throw new UnsupportedOperationException("OpenGLES 3.1 is not supported!");
        }
    }

    @Override
    public void bufferBindUniform(GLES3XBuffer bt, int index) {
        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, index, bt.bufferId);
    }

    @Override
    public void bufferBindUniform(GLES3XBuffer bt, int index, long offset, long size) {
        GLES30.glBindBufferRange(GLES30.GL_UNIFORM_BUFFER, index, bt.bufferId, offset, size);
    }

    @Override
    public void bufferCopyData(GLES3XBuffer srcBuffer, long srcOffset, GLES3XBuffer dstBuffer, long dstOffset, long size) {
        GLES20.glBindBuffer(GLES30.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
        GLES20.glBindBuffer(GLES30.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);

        GLES30.glCopyBufferSubData(GLES30.GL_COPY_READ_BUFFER, GLES30.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);
    }

    @Override
    public GLES3XBuffer bufferCreate() {
        final GLES3XBuffer buffer = new GLES3XBuffer();
        buffer.bufferId = GLES20.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GLES3XBuffer buffer) {
        if (buffer.isValid()) {
            GLES20.glDeleteBuffers(buffer.bufferId);
            buffer.bufferId = -1;
            buffer.usage = 0;
            buffer.access = 0;
            buffer.size = 0;
        }
    }

    @Override
    public void bufferGetData(GLES3XBuffer buffer, long offset, ByteBuffer out) {
        final ByteBuffer inData = this.bufferMapData(buffer, offset, out.limit(), GLES30.GL_MAP_READ_BIT);
        out.put(inData).flip();
        this.bufferUnmapData(buffer);
    }

    @Override
    public int bufferGetMaxUniformBindings() {
        return GLES20.glGetInteger(GLES20.GL_MAX_FRAGMENT_UNIFORM_VECTORS);
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        return GLES20.glGetInteger(GLES30.GL_MAX_UNIFORM_BLOCK_SIZE);
    }

    @Override
    public int bufferGetParameterI(GLES3XBuffer buffer, int paramId) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        return GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
    }

    @Override
    public void bufferInvalidateData(GLES3XBuffer buffer) {
        if (buffer.usage != 0 && buffer.access == 0) {
            bufferAllocate(buffer, buffer.size, buffer.usage);
        } else {
            bufferAllocateImmutable(buffer, buffer.size, buffer.access); // is this right?
        }
    }

    @Override
    public void bufferInvalidateRange(GLES3XBuffer buffer, long offset, long length) {
        bufferInvalidateData(buffer);
    }

    @Override
    public ByteBuffer bufferMapData(GLES3XBuffer buffer, long offset, long length, int accessFlags) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = GLES30.glMapBufferRange(GLES20.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GLES3XBuffer buffer, ByteBuffer data, int usage) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data, usage);
    }

    @Override
    public void bufferUnmapData(GLES3XBuffer buffer) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES30.glUnmapBuffer(GLES20.GL_ARRAY_BUFFER);
    }

    @Override
    public void clear(int bitfield, float red, float green, float blue, float alpha, double depth) {
        GLES20.glClearColor(red, green, blue, alpha);
        GLES20.glClearDepthf((float) depth);
        GLES20.glClear(bitfield);
    }

    @Override
    public void depthTestDisable() {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void depthTestEnable(int depthTest) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(depthTest);
    }    

    @Override
    public void framebufferAddAttachment(GLES3XFramebuffer framebuffer, int attachmentId, GLES3XTexture texId, int mipmapLevel) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentId, GLES20.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddRenderbuffer(GLES3XFramebuffer ft, int attachmentId, GLES3XRenderbuffer rt) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, attachmentId, GLES20.GL_RENDERBUFFER, rt.renderbufferId);
    }

    @Override
    public void framebufferBind(GLES3XFramebuffer framebuffer, IntBuffer attachments) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GLES30.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GLES3XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GLES3XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        GLES20.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GLES30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);
    }

    @Override
    public GLES3XFramebuffer framebufferCreate() {
        final GLES3XFramebuffer fb = new GLES3XFramebuffer();
        fb.framebufferId = GLES20.glGenFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(GLES3XFramebuffer framebuffer) {
        if (framebuffer.isValid()) {
            GLES20.glDeleteFramebuffers(framebuffer.framebufferId);
            framebuffer.framebufferId = -1;
        }
    }

    @Override
    public GLES3XFramebuffer framebufferGetDefault() {
        final GLES3XFramebuffer fb = new GLES3XFramebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GLES3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, GLES3XBuffer dstBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
        GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);

        GLES20.glReadPixels(
                x, y, width, height,
                format, type,
                0L);
    }

    @Override
    public void framebufferGetPixels(GLES3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GLES20.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);
    }

    @Override
    public boolean framebufferIsComplete(GLES3XFramebuffer framebuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
        final int complete = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        return complete == GLES20.GL_FRAMEBUFFER_COMPLETE;
    }

    @Override
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, int stencil) {
        GLES20.glColorMask(red, green, blue, alpha);
        GLES20.glDepthMask(depth);
        GLES20.glStencilMask(stencil);
    }

    @Override
    public void polygonSetParameters(float pointSize, float lineWidth, int frontFace, int cullFace, int polygonMode, float offsetFactor, float offsetUnits) {
        GLES20.glLineWidth(lineWidth);
        GLES20.glFrontFace(frontFace);

        if (cullFace == 0) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        } else {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(cullFace);
        }

        GLES20.glPolygonOffset(offsetFactor, offsetUnits);
    }

    @Override
    public GLES3XProgram programCreate() {
        GLES3XProgram program = new GLES3XProgram();
        program.programId = GLES20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GLES3XProgram program) {
        if (program.isValid()) {
            GLES20.glDeleteProgram(program.programId);
            program.programId = -1;
        }
    }

    @Override
    public void programDispatchCompute(GLES3XProgram program, int numX, int numY, int numZ) {
        if (GLES.getCapabilities().GLES31) {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            GLES20.glUseProgram(program.programId);
            GLES31.glDispatchCompute(numX, numY, numZ);
            GLES20.glUseProgram(currentProgram);
        } else {
            throw new UnsupportedOperationException("Compute shaders are not supported!");
        }
    }

    @Override
    public int programGetStorageBlockBinding(GLES3XProgram pt, String storageName) {
        if (pt.storageBindings.containsKey(storageName)) {
            return pt.storageBindings.get(storageName);
        } else {
            return -1;
        }
    }

    @Override
    public int programGetUniformBlockBinding(GLES3XProgram pt, String uniformBlockName) {
        if (pt.uniformBindings.containsKey(uniformBlockName)) {
            return pt.uniformBindings.get(uniformBlockName);
        } else {
            return -1;
        }
    }

    @Override
    public int programGetUniformLocation(GLES3XProgram program, String name) {        
        return GLES20.glGetUniformLocation(program.programId, name);
    }

    @Override
    public void programLinkShaders(GLES3XProgram program, Shader[] shaders) {
        for (Shader shader : shaders) {
            GLES20.glAttachShader(program.programId, ((GLES3XShader) shader).shaderId);
        }

        GLES20.glLinkProgram(program.programId);

        for (Shader shader : shaders) {
            GLES20.glDetachShader(program.programId, ((GLES3XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GLES3XProgram program, int index, String name) {
        GLES20.glBindAttribLocation(program.programId, index, name);
    }    

    @Override
    public void programSetFeedbackVaryings(GLES3XProgram program, String[] varyings) {
        GLES30.glTransformFeedbackVaryings(program.programId, varyings, GLES30.GL_SEPARATE_ATTRIBS);
    }    

    @Override
    public void programSetStorageBlockBinding(GLES3XProgram pt, String uniformName, int binding) {
        final int sBlockIndex = GLES30.glGetUniformBlockIndex(pt.programId, uniformName);

        GLES30.glUniformBlockBinding(pt.programId, sBlockIndex, binding);
        pt.storageBindings.put(uniformName, binding);
    }

    @Override
    public void programSetUniformBlockBinding(GLES3XProgram pt, String uniformBlockName, int binding) {
        final int uBlockIndex = GLES30.glGetUniformBlockIndex(pt.programId, uniformBlockName);

        GLES30.glUniformBlockBinding(pt.programId, uBlockIndex, binding);
        pt.uniformBindings.put(uniformBlockName, binding);
    }

    @Override
    public void programSetUniformD(GLES3XProgram program, int uLoc, double[] value) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformF(GLES3XProgram program, int uLoc, float[] value) {
        if (GLES.getCapabilities().GLES31) {
            switch (value.length) {
                case 1:
                    GLES31.glProgramUniform1f(program.programId, uLoc, value[0]);
                    break;
                case 2:
                    GLES31.glProgramUniform2f(program.programId, uLoc, value[0], value[1]);
                    break;
                case 3:
                    GLES31.glProgramUniform3f(program.programId, uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GLES31.glProgramUniform4f(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                    break;
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform1f(uLoc, value[0]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform2f(uLoc, value[0], value[1]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform3f(uLoc, value[0], value[1], value[2]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform4f(uLoc, value[0], value[1], value[2], value[3]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformI(GLES3XProgram program, int uLoc, int[] value) {
        if (GLES.getCapabilities().GLES31) {
            switch (value.length) {
                case 1:
                    GLES31.glProgramUniform1i(program.programId, uLoc, value[0]);
                    break;
                case 2:
                    GLES31.glProgramUniform2i(program.programId, uLoc, value[0], value[1]);
                    break;
                case 3:
                    GLES31.glProgramUniform3i(program.programId, uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GLES31.glProgramUniform4i(program.programId, uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform1i(uLoc, value[0]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform2i(uLoc, value[0], value[1]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform3i(uLoc, value[0], value[1], value[2]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform4i(uLoc, value[0], value[1], value[2], value[3]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformMatD(GLES3XProgram program, int uLoc, DoubleBuffer mat) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformMatF(GLES3XProgram program, int uLoc, FloatBuffer mat) {
        if (GLES.getCapabilities().GLES31) {
            switch (mat.limit()) {
                case 4:
                    GLES31.glProgramUniformMatrix2fv(program.programId, uLoc, false, mat);
                    break;
                case 9:
                    GLES31.glProgramUniformMatrix3fv(program.programId, uLoc, false, mat);
                    break;
                case 16:
                    GLES31.glProgramUniformMatrix4fv(program.programId, uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix2fv(uLoc, false, mat);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix3fv(uLoc, false, mat);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix4fv(uLoc, false, mat);
                    GLES20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        }
    }

    @Override
    public void programUse(GLES3XProgram program) {
        GLES20.glUseProgram(program.programId);
    }

    @Override
    public GLES3XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        final GLES3XRenderbuffer out = new GLES3XRenderbuffer();

        out.renderbufferId = GLES20.glGenRenderbuffers();

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, out.renderbufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, internalFormat, width, height);

        return out;
    }

    @Override
    public void renderbufferDelete(GLES3XRenderbuffer rt) {
        if (rt.isValid()) {
            GLES20.glDeleteRenderbuffers(rt.renderbufferId);
            rt.renderbufferId = -1;
        }
    }

    @Override
    public void samplerBind(int unit, GLES3XSampler sampler) {
        GLES30.glBindSampler(unit, sampler.samplerId);
    }

    @Override
    public GLES3XSampler samplerCreate() {
        final GLES3XSampler sampler = new GLES3XSampler();
        sampler.samplerId = GLES30.glGenSamplers();
        return sampler;
    }

    @Override
    public void samplerDelete(GLES3XSampler sampler) {
        if (sampler.isValid()) {
            GLES30.glDeleteSamplers(sampler.samplerId);
            sampler.samplerId = -1;
        }
    }

    @Override
    public void samplerSetParameter(GLES3XSampler sampler, int param, int value) {
        GLES30.glSamplerParameteri(sampler.samplerId, param, value);
    }

    @Override
    public void samplerSetParameter(GLES3XSampler sampler, int param, float value) {
        GLES30.glSamplerParameterf(sampler.samplerId, param, value);
    }

    @Override
    public void scissorTestDisable() {
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(int left, int bottom, int width, int height) {
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(left, bottom, width, height);
    }

    @Override
    public GLES3XShader shaderCompile(int type, String source) {
        final GLES3XShader shader = new GLES3XShader();

        shader.shaderId = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader.shaderId, source);
        GLES20.glCompileShader(shader.shaderId);
        return shader;
    }

    @Override
    public void shaderDelete(GLES3XShader shader) {
        if (shader.isValid()) {
            GLES20.glDeleteShader(shader.shaderId);
            shader.shaderId = -1;
        }
    }

    @Override
    public String shaderGetInfoLog(GLES3XShader shader) {
        return GLES20.glGetShaderInfoLog(shader.shaderId);
    }

    @Override
    public int shaderGetParameterI(GLES3XShader shader, int pName) {
        return GLES20.glGetShaderi(shader.shaderId, pName);
    }

    @Override
    public int shaderGetVersion() {
        final GLESCapabilities caps = GLES.getCapabilities();
        //TODO: this isn't actually correct...

        if (caps.GLES32) {
            return 320;
        } else if (caps.GLES31) {
            return 310;
        } else {
            return 300;
        }
    }

    @Override
    public GLES3XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth, int dataType) {
        final int target;

        if (width < 1 || height < 1 || depth < 1) {
            throw new IllegalArgumentException("Invalid dimensions!");
        } else if (width >= 1 && height == 1 && depth == 1) {
            target = GLES20.GL_TEXTURE_2D;
        } else if (width >= 1 && height > 1 && depth == 1) {
            target = GLES20.GL_TEXTURE_2D;
        } else if (width >= 1 && height >= 1 && depth > 1) {
            target = GLES30.GL_TEXTURE_3D;
        } else {
            throw new IllegalArgumentException("Invalid dimensions!");
        }

        final GLES3XTexture texture = new GLES3XTexture();

        texture.textureId = GLES20.glGenTextures();
        texture.target = target;
        texture.internalFormat = internalFormat;

        switch (target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAX_LEVEL, mipmaps);

                GLES30.glTexStorage2D(GLES20.GL_TEXTURE_2D, mipmaps, internalFormat, width, height);

                break;
            case GLES30.GL_TEXTURE_3D:
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MAX_LEVEL, mipmaps);

                GLES30.glTexStorage3D(GLES30.GL_TEXTURE_3D, mipmaps, internalFormat, width, height, depth);                

                break;
        }

        return texture;
    }

    @Override
    public void textureBind(GLES3XTexture texture, int unit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
        GLES20.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDelete(GLES3XTexture texture) {
        if (texture.isValid()) {
            GLES20.glDeleteTextures(texture.textureId);
            texture.textureId = -1;
        }
    }

    @Override
    public void textureGenerateMipmap(GLES3XTexture texture) {
        GLES20.glBindTexture(texture.target, texture.textureId);
        GLES20.glGenerateMipmap(texture.target);
    }

    @Override
    public void textureGetData(GLES3XTexture texture, int level, int format, int type, ByteBuffer out) {
        throw new UnsupportedOperationException("textureGetData is not supported!");
    }

    @Override
    public float textureGetMaxAnisotropy() {
        if (GLES.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            return GLES20.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        } else {
            return 1.0f;
        }
    }

    @Override
    public int textureGetMaxBoundTextures() {
        return GLES20.glGetInteger(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public int textureGetMaxSize() {
        return GLES20.glGetInteger(GLES20.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return GLES20.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GLES3XTexture texture, int level) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public void textureInvalidateRange(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public long textureMap(GLES3XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 3.0 does not support bindless textures!");
    }

    @Override
    public void textureSetData(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D: {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);

                if (format == GL_BGRA_EXT && !GLES.getCapabilities().GL_EXT_texture_format_BGRA8888) {
                    final int size = data.capacity();
                    final ByteBuffer rgbaData = MemoryUtil.memAlloc(size);

                    CommonUtils.rbPixelSwap(rgbaData, data);
                    GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, GLES20.GL_RGBA, type, rgbaData);
                    MemoryUtil.memFree(rgbaData);
                } else {
                    GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                }
            }
            break;
            case GLES30.GL_TEXTURE_3D: {
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);

                if (format == GL_BGRA_EXT && !GLES.getCapabilities().GL_EXT_texture_format_BGRA8888) {
                    final int size = data.capacity();
                    final ByteBuffer rgbaData = MemoryUtil.memAlloc(size);

                    CommonUtils.rbPixelSwap(rgbaData, data);
                    GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, GLES20.GL_RGBA, type, rgbaData);
                    MemoryUtil.memFree(rgbaData);
                } else {
                    GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                }
            }
            break;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GLES3XDriver.class);

    @Override
    public void textureSetParameter(GLES3XTexture texture, int param, int value) {
        switch (param) {
            case GLES30.GL_TEXTURE_BASE_LEVEL:
            case GLES30.GL_TEXTURE_COMPARE_FUNC:
            case GLES30.GL_TEXTURE_COMPARE_MODE:
            case GLES20.GL_TEXTURE_MIN_FILTER:
            case GLES20.GL_TEXTURE_MAG_FILTER:
            case GLES30.GL_TEXTURE_MIN_LOD:
            case GLES30.GL_TEXTURE_MAX_LEVEL:
            case GLES30.GL_TEXTURE_SWIZZLE_R:
            case GLES30.GL_TEXTURE_SWIZZLE_G:
            case GLES30.GL_TEXTURE_SWIZZLE_B:
            case GLES30.GL_TEXTURE_SWIZZLE_A:
            case GLES30.GL_TEXTURE_WRAP_R:
            case GLES20.GL_TEXTURE_WRAP_S:
            case GLES20.GL_TEXTURE_WRAP_T:
                GLES20.glBindTexture(texture.target, texture.textureId);
                GLES20.glTexParameteri(texture.target, param, value);
                break;
            default:
                LOGGER.trace("Unsupported parameter name: {}", param);
        }
    }

    @Override
    public void textureSetParameter(GLES3XTexture texture, int param, float value) {
        switch (param) {
            case EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT:
                if (!GLES.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    LOGGER.trace("EXT_texture_filter_anisotropic is not supported!");
                    return;
                }
            case GLES30.GL_TEXTURE_BASE_LEVEL:
            case GLES30.GL_TEXTURE_COMPARE_FUNC:
            case GLES30.GL_TEXTURE_COMPARE_MODE:
            case GLES20.GL_TEXTURE_MIN_FILTER:
            case GLES20.GL_TEXTURE_MAG_FILTER:
            case GLES30.GL_TEXTURE_MIN_LOD:
            case GLES30.GL_TEXTURE_MAX_LEVEL:
            case GLES30.GL_TEXTURE_SWIZZLE_R:
            case GLES30.GL_TEXTURE_SWIZZLE_G:
            case GLES30.GL_TEXTURE_SWIZZLE_B:
            case GLES30.GL_TEXTURE_SWIZZLE_A:
            case GLES30.GL_TEXTURE_WRAP_R:
            case GLES20.GL_TEXTURE_WRAP_S:
            case GLES20.GL_TEXTURE_WRAP_T:
                GLES20.glBindTexture(texture.target, texture.textureId);
                GLES20.glTexParameterf(texture.target, param, value);
                break;
            default:
                LOGGER.trace("Unsupported parameter name: {}", param);
        }
    }

    @Override
    public void textureUnmap(GLES3XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 3.0 does not support bindless textures!");
    }

    @Override
    public void transformFeedbackBegin(int primitiveMode) {
        GLES20.glEnable(GLES30.GL_RASTERIZER_DISCARD);
        GLES30.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void transformFeedbackEnd() {
        GLES30.glEndTransformFeedback();
        GLES20.glDisable(GLES30.GL_RASTERIZER_DISCARD);
    }

    @Override
    public void vertexArrayAttachBuffer(GLES3XVertexArray vao, int index, GLES3XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        GLES30.glBindVertexArray(vao.vertexArrayId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glVertexAttribPointer(index, size, type, false, stride, offset);
        GLES20.glEnableVertexAttribArray(index);

        if (divisor > 0) {
            GLES30.glVertexAttribDivisor(index, divisor);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GLES3XVertexArray vao, GLES3XBuffer buffer) {
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
    }

    @Override
    public GLES3XVertexArray vertexArrayCreate() {
        final GLES3XVertexArray vao = new GLES3XVertexArray();
        vao.vertexArrayId = GLES30.glGenVertexArrays();
        return vao;
    }

    @Override
    public void vertexArrayDelete(GLES3XVertexArray vao) {
        if (vao.isValid()) {
            GLES30.glDeleteVertexArrays(vao.vertexArrayId);
            vao.vertexArrayId = -1;
        }
    }

    @Override
    public void vertexArrayDrawArrays(GLES3XVertexArray vao, int drawMode, int start, int count) {
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawArrays(drawMode, start, count);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, int drawMode, long offset) {
        if (GLES.getCapabilities().GLES31) {
            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawArraysIndirect(drawMode, offset);
        } else {
            throw new UnsupportedOperationException("Draw Arrays Indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GLES3XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES30.glDrawArraysInstanced(drawMode, first, count, instanceCount);
    }

    @Override
    public void vertexArrayDrawElements(GLES3XVertexArray vao, int drawMode, int count, int type, long offset) {
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawElements(drawMode, count, type, offset);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        if (GLES.getCapabilities().GLES31) {
            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawElementsIndirect(drawMode, indexType, offset);
        } else {
            throw new UnsupportedOperationException("Draw Elements Indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GLES3XVertexArray vao, int drawMode, int count, int type, long offset, int instanceCount) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES30.glDrawElementsInstanced(drawMode, count, type, offset, instanceCount);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    public void textureGetData(GLES3XTexture texture, int level, int format, int type, GLES3XBuffer out, long offset, int size) {
        throw new UnsupportedOperationException("OpenGLES does not support texture downloads!");
    }

    @Override
    public void textureSetData(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, GLES3XBuffer buffer, long offset) {
        final int currentTex;
        
        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D:
                currentTex = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, buffer.bufferId);
                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, 0L);
                GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTex);
                break;
            case GLES30.GL_TEXTURE_3D:
                currentTex = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, buffer.bufferId);
                GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, 0L);
                GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, 0);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, currentTex);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }
    }

}
