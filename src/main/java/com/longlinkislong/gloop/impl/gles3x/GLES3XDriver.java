/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengles.EXTBufferStorage;
import org.lwjgl.opengles.EXTTextureFilterAnisotropic;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLES30;
import org.lwjgl.opengles.GLES31;

/**
 *
 * @author zmichaels
 */
public final class GLES3XDriver implements Driver<
        GLES3XBuffer, GLES3XFramebuffer, GLES3XRenderbuffer, GLES3XTexture, GLES3XShader, GLES3XProgram, GLES3XSampler, GLES3XVertexArray, GLES3XDrawQuery> {

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
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, usage);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferAllocateImmutable(GLES3XBuffer buffer, long size, int bitflags) {
        if (GLES.getCapabilities().GL_EXT_buffer_storage) {
            final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
            EXTBufferStorage.glBufferStorageEXT(GLES20.GL_ARRAY_BUFFER, size, bitflags);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
        } else {
            bufferAllocate(buffer, size, GLES20.GL_DYNAMIC_DRAW);
        }
    }

    @Override
    public void bufferBindAtomic(GLES3XBuffer bt, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bufferBindAtomic(GLES3XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        final int currentCopyRead = GLES20.glGetInteger(GLES30.GL_COPY_READ_BUFFER_BINDING);
        final int currentCopyWrite = GLES20.glGetInteger(GLES30.GL_COPY_WRITE_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES30.GL_COPY_READ_BUFFER, srcBuffer.bufferId);
        GLES20.glBindBuffer(GLES30.GL_COPY_WRITE_BUFFER, dstBuffer.bufferId);

        GLES30.glCopyBufferSubData(GLES30.GL_COPY_READ_BUFFER, GLES30.GL_COPY_WRITE_BUFFER, srcOffset, dstOffset, size);

        GLES20.glBindBuffer(GLES30.GL_COPY_READ_BUFFER, currentCopyRead);
        GLES20.glBindBuffer(GLES30.GL_COPY_WRITE_BUFFER, currentCopyWrite);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int bufferGetParameterI(GLES3XBuffer buffer, int paramId) {
        final int currentAB = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        final int res = GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentAB);
        return res;
    }

    @Override
    public void bufferInvalidateData(GLES3XBuffer buffer) {
        final int currentAB = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);

        final int size = GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, GLES20.GL_BUFFER_SIZE);
        final int usage = GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, GLES20.GL_BUFFER_USAGE);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, usage);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentAB);
    }

    @Override
    public void bufferInvalidateRange(GLES3XBuffer buffer, long offset, long length) {
        bufferInvalidateData(buffer);
    }

    @Override
    public ByteBuffer bufferMapData(GLES3XBuffer buffer, long offset, long length, int accessFlags) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = GLES30.glMapBufferRange(GLES20.GL_ARRAY_BUFFER, offset, length, accessFlags, buffer.mapBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GLES3XBuffer buffer, ByteBuffer data, int usage) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data, usage);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferUnmapData(GLES3XBuffer buffer) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES30.glUnmapBuffer(GLES20.GL_ARRAY_BUFFER);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
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
    public void drawQueryBeginConditionalRender(GLES3XDrawQuery query, int mode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GLES3XDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GLES3XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(int condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int condition, GLES3XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GLES3XFramebuffer framebuffer, int attachmentId, GLES3XTexture texId, int mipmapLevel) {
        final int currentFb = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentId, GLES20.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddRenderbuffer(GLES3XFramebuffer ft, int i, GLES3XRenderbuffer rt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        final int currentReadFb = GLES20.glGetInteger(GLES30.GL_READ_FRAMEBUFFER_BINDING);
        final int currentDrawFb = GLES20.glGetInteger(GLES30.GL_DRAW_FRAMEBUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GLES30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);

        GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, currentDrawFb);
        GLES20.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, currentReadFb);
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
        final int currentFB = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);
        final int currentBuffer = GLES20.glGetInteger(GLES30.GL_PIXEL_PACK_BUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GLES20.glReadPixels(
                x, y, width, height,
                format, type,
                0L);
        GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, currentBuffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GLES3XFramebuffer framebuffer, int x, int y, int width, int height, int format, int type, ByteBuffer dstBuffer) {
        final int currentFB = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GLES20.glReadPixels(
                x, y, width, height,
                format, type,
                dstBuffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public boolean framebufferIsComplete(GLES3XFramebuffer framebuffer) {
        final int currentFb = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
        final int complete = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFb);
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
    public int programGetStorageBlockBinding(GLES3XProgram pt, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int programGetUniformBlockBinding(GLES3XProgram pt, String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int programGetUniformLocation(GLES3XProgram program, String name) {
        final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

        GLES20.glUseProgram(program.programId);
        final int res = GLES20.glGetUniformLocation(program.programId, name);
        GLES20.glUseProgram(currentProgram);
        return res;
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
    public void programSetFeedbackBuffer(GLES3XProgram program, int varyingLoc, GLES3XBuffer buffer) {
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GLES3XProgram program, String[] varyings) {
        GLES30.glTransformFeedbackVaryings(program.programId, varyings, GLES30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GLES3XProgram program, String storageName, GLES3XBuffer buffer, int bindingPoint) {
        throw new UnsupportedOperationException("Shader storage is not supported!");
    }

    @Override
    public void programSetStorageBlockBinding(GLES3XProgram pt, String uniformName, int binding) {
        throw new UnsupportedOperationException("Not yet supported...");
    }

    @Override
    public void programSetUniformBlock(GLES3XProgram program, String uniformName, GLES3XBuffer buffer, int bindingPoint) {
        final int uBlock = GLES30.glGetUniformBlockIndex(program.programId, uniformName);

        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, bindingPoint, buffer.bufferId);
        GLES30.glUniformBlockBinding(program.programId, uBlock, bindingPoint);
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

        final int currentRB = GLES20.glGetInteger(GLES20.GL_RENDERBUFFER_BINDING);

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, out.renderbufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, internalFormat, width, height);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, currentRB);

        return out;
    }

    @Override
    public void renderbufferDelete(GLES3XRenderbuffer rt) {
        GLES20.glDeleteRenderbuffers(rt.renderbufferId);
        rt.renderbufferId = -1;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GLES3XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth) {
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

        int currentTexture;
        switch (target) {
            case GLES20.GL_TEXTURE_2D:
                currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), GLES20.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                }

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
                break;
            case GLES30.GL_TEXTURE_3D:
                currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MAX_LEVEL, mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GLES30.glTexImage3D(GLES30.GL_TEXTURE_3D, i, internalFormat, width, height, depth, 0, guessFormat(internalFormat), GLES20.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                    depth = Math.max(1, (depth / 2));
                }

                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, currentTexture);
                break;
        }

        return texture;
    }

    @Override
    public void textureAllocatePage(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public void textureBind(GLES3XTexture texture, int unit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
        GLES20.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
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
        final int binding;

        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D:
                binding = GLES20.GL_TEXTURE_BINDING_2D;
                break;
            case GLES30.GL_TEXTURE_3D:
                binding = GLES30.GL_TEXTURE_BINDING_3D;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        final int currentTexture = GLES20.glGetInteger(binding);

        GLES20.glBindTexture(texture.target, texture.textureId);
        GLES20.glGenerateMipmap(texture.target);
        GLES20.glBindTexture(texture.target, currentTexture);
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
    public int textureGetPageDepth(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public int textureGetPageHeight(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public int textureGetPageWidth(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void textureSetData(GLES3XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D: {
                final int currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GLES30.GL_TEXTURE_3D: {
                final int currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);

                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_3D, level, xOffset, yOffset, zOffset, width, height, depth, format, type, data);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, currentTexture);
            }
            break;

        }
    }

    @Override
    public void textureSetParameter(GLES3XTexture texture, int param, int value) {
        final int currentTexture;

        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D:
                currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);
                break;
            case GLES30.GL_TEXTURE_3D:
                currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        GLES20.glBindTexture(texture.target, texture.textureId);
        GLES20.glTexParameteri(texture.target, param, value);
        GLES20.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureSetParameter(GLES3XTexture texture, int param, float value) {
        final int currentTexture;

        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D:
                currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);
                break;
            case GLES30.GL_TEXTURE_3D:
                currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target: " + texture.target);
        }

        GLES20.glBindTexture(texture.target, texture.textureId);
        GLES20.glTexParameterf(texture.target, param, value);
        GLES20.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureUnmap(GLES3XTexture tt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transformFeedbackBegin(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void transformFeedbackEnd() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void vertexArrayAttachBuffer(GLES3XVertexArray vao, int index, GLES3XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glEnableVertexAttribArray(index);

        GLES20.glVertexAttribPointer(index, size, type, false, stride, offset);

        if (divisor > 0) {
            GLES30.glVertexAttribDivisor(index, divisor);
        }

        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GLES3XVertexArray vao, GLES3XBuffer buffer) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.bufferId);
        GLES30.glBindVertexArray(currentVao);
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
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawArrays(drawMode, start, count);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, int drawMode, long offset) {
        if (GLES.getCapabilities().GLES31) {
            final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
            final int currentIndirect = GLES20.glGetInteger(GLES31.GL_DRAW_INDIRECT_BUFFER_BINDING);

            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawArraysIndirect(drawMode, offset);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
            GLES30.glBindVertexArray(currentVao);
        } else {
            throw new UnsupportedOperationException("Draw Arrays Indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GLES3XVertexArray vao, int drawMode, int first, int count, int instanceCount) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES30.glDrawArraysInstanced(drawMode, first, count, instanceCount);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(GLES3XVertexArray vao, int drawMode, int count, int type, long offset) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawElements(drawMode, count, type, offset);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, int drawMode, int indexType, long offset) {
        if (GLES.getCapabilities().GLES31) {
            final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
            final int currentIndirect = GLES20.glGetInteger(GLES31.GL_DRAW_INDIRECT_BUFFER_BINDING);

            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawElementsIndirect(drawMode, indexType, offset);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
            GLES30.glBindVertexArray(currentVao);
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

}
