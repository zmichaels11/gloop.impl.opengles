/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.spi.Driver;
import com.longlinkislong.gloop.spi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengles.EXTBufferStorage;
import org.lwjgl.opengles.EXTMultiDrawArrays;
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
        GLES3XBuffer, GLES3XFramebuffer, GLES3XTexture, GLES3XShader, GLES3XProgram, GLES3XSampler, GLES3XVertexArray, GLES3XDrawQuery> {

    @Override
    public void blendingDisable() {
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void blendingEnable(long rgbEq, long aEq, long rgbFuncSrc, long rgbFuncDst, long aFuncSrc, long aFuncDst) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate((int) rgbFuncSrc, (int) rgbFuncDst, (int) aFuncSrc, (int) aFuncDst);
        GLES20.glBlendEquationSeparate((int) rgbEq, (int) aEq);
    }

    @Override
    public void bufferAllocate(GLES3XBuffer buffer, long size, long usage) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, (int) usage);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
    }

    @Override
    public void bufferAllocateImmutable(GLES3XBuffer buffer, long size, long bitflags) {
        if (GLES.getCapabilities().GL_EXT_buffer_storage) {
            final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
            EXTBufferStorage.glBufferStorageEXT(GLES20.GL_ARRAY_BUFFER, (int) size, (int) bitflags);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
        } else {
            throw new UnsupportedOperationException("Buffer storage is not supported!");
        }
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
    public long bufferGetParameter(GLES3XBuffer buffer, long paramId) {
        final int currentAB = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        final int res = GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentAB);
        return res;
    }

    @Override
    public void bufferInvalidateData(GLES3XBuffer buffer) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public void bufferInvalidateRange(GLES3XBuffer buffer, long offset, long length) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public ByteBuffer bufferMapData(GLES3XBuffer buffer, long offset, long length, long accessFlags) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        buffer.mapBuffer = GLES30.glMapBufferRange(GLES20.GL_ARRAY_BUFFER, offset, length, (int) accessFlags, buffer.mapBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, currentBuffer);
        return buffer.mapBuffer;
    }

    @Override
    public void bufferSetData(GLES3XBuffer buffer, ByteBuffer data, long usage) {
        final int currentBuffer = GLES20.glGetInteger(GLES20.GL_ARRAY_BUFFER_BINDING);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, data, (int) usage);
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
    public void clear(long bitfield, double red, double green, double blue, double alpha, double depth) {
        GLES20.glClearColor((float) red, (float) green, (float) blue, (float) alpha);
        GLES20.glClearDepthf((float) depth);
        GLES20.glClear((int) bitfield);
    }

    @Override
    public void depthTestDisable() {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void depthTestEnable(long depthTest) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc((int) depthTest);
    }

    @Override
    public void drawQueryBeginConditionalRender(GLES3XDrawQuery query, long mode) {
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
    public void drawQueryDisable(long condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(long condition, GLES3XDrawQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GLES3XFramebuffer framebuffer, long attachmentId, GLES3XTexture texId, long mipmapLevel) {
        final int currentFb = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, (int) attachmentId, GLES20.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthAttachment(GLES3XFramebuffer framebuffer, GLES3XTexture texId, long mipmapLevel) {
        final int currentFb = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferAddDepthStencilAttachment(GLES3XFramebuffer framebuffer, GLES3XTexture texId, long mipmapLevel) {
        final int currentFb = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES20.GL_TEXTURE_2D, texId.textureId, (int) mipmapLevel);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFb);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported texture target!");
        }
    }

    @Override
    public void framebufferBind(GLES3XFramebuffer framebuffer, IntBuffer attachments) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        if (attachments != null) {
            GLES30.glDrawBuffers(attachments);
        }
    }

    @Override
    public void framebufferBlit(GLES3XFramebuffer srcFb, long srcX0, long srcY0, long srcX1, long srcY1, GLES3XFramebuffer dstFb, long dstX0, long dstY0, long dstX1, long dstY1, long bitfield, long filter) {
        final int currentReadFb = GLES20.glGetInteger(GLES30.GL_READ_FRAMEBUFFER_BINDING);
        final int currentDrawFb = GLES20.glGetInteger(GLES30.GL_DRAW_FRAMEBUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES30.GL_READ_FRAMEBUFFER, srcFb.framebufferId);
        GLES20.glBindFramebuffer(GLES30.GL_DRAW_FRAMEBUFFER, dstFb.framebufferId);

        GLES30.glBlitFramebuffer((int) srcX0, (int) srcY0, (int) srcX1, (int) srcY1, (int) dstX0, (int) dstY0, (int) dstX1, (int) dstY1, (int) bitfield, (int) filter);

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
    public void framebufferGetPixels(GLES3XFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, GLES3XBuffer dstBuffer) {
        final int currentFB = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);
        final int currentBuffer = GLES20.glGetInteger(GLES30.GL_PIXEL_PACK_BUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, dstBuffer.bufferId);
        GLES20.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
                0L);
        GLES20.glBindBuffer(GLES30.GL_PIXEL_PACK_BUFFER, currentBuffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, currentFB);
    }

    @Override
    public void framebufferGetPixels(GLES3XFramebuffer framebuffer, long x, long y, long width, long height, long format, long type, ByteBuffer dstBuffer) {
        final int currentFB = GLES20.glGetInteger(GLES20.GL_FRAMEBUFFER_BINDING);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.framebufferId);

        GLES20.glReadPixels(
                (int) x, (int) y, (int) width, (int) height,
                (int) format, (int) type,
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
    public void maskApply(boolean red, boolean green, boolean blue, boolean alpha, boolean depth, long stencil) {
        GLES20.glColorMask(red, green, blue, alpha);
        GLES20.glDepthMask(depth);
        GLES20.glStencilMask((int) stencil);
    }

    @Override
    public void polygonSetParameters(double pointSize, double lineWidth, long frontFace, long cullFace, long polygonMode, double offsetFactor, double offsetUnits) {
        GLES20.glLineWidth((float) lineWidth);
        GLES20.glFrontFace((int) frontFace);

        if (cullFace == 0) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        } else {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace((int) cullFace);
        }

        GLES20.glPolygonOffset((float) offsetFactor, (float) offsetUnits);
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
    public void programDispatchCompute(GLES3XProgram program, long numX, long numY, long numZ) {
        if (GLES.getCapabilities().GLES31) {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            GLES20.glUseProgram(program.programId);
            GLES31.glDispatchCompute((int) numX, (int) numY, (int) numZ);
            GLES20.glUseProgram(currentProgram);
        } else {
            throw new UnsupportedOperationException("Compute shaders are not supported!");
        }
    }

    @Override
    public long programGetUniformLocation(GLES3XProgram program, String name) {
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
    public void programSetAttribLocation(GLES3XProgram program, long index, String name) {
        GLES20.glBindAttribLocation(program.programId, (int) index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GLES3XProgram program, long varyingLoc, GLES3XBuffer buffer) {
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, (int) varyingLoc, buffer.bufferId);
    }

    @Override
    public void programSetFeedbackVaryings(GLES3XProgram program, String[] varyings) {
        GLES30.glTransformFeedbackVaryings(program.programId, varyings, GLES30.GL_SEPARATE_ATTRIBS);
    }

    @Override
    public void programSetStorage(GLES3XProgram program, String storageName, GLES3XBuffer buffer, long bindingPoint) {
        throw new UnsupportedOperationException("Shader storage is not supported!");
    }

    @Override
    public void programSetUniformBlock(GLES3XProgram program, String uniformName, GLES3XBuffer buffer, long bindingPoint) {
        final int uBlock = GLES30.glGetUniformBlockIndex(program.programId, uniformName);

        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, (int) bindingPoint, buffer.bufferId);
        GLES30.glUniformBlockBinding(program.programId, uBlock, (int) bindingPoint);
    }

    @Override
    public void programSetUniformD(GLES3XProgram program, long uLoc, double[] value) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformF(GLES3XProgram program, long uLoc, float[] value) {
        if (GLES.getCapabilities().GLES31) {
            switch (value.length) {
                case 1:
                    GLES31.glProgramUniform1f(program.programId, (int) uLoc, value[0]);
                    break;
                case 2:
                    GLES31.glProgramUniform2f(program.programId, (int) uLoc, value[0], value[1]);
                    break;
                case 3:
                    GLES31.glProgramUniform3f(program.programId, (int) uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GLES31.glProgramUniform4f(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                    break;
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform1f((int) uLoc, value[0]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform2f((int) uLoc, value[0], value[1]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform3f((int) uLoc, value[0], value[1], value[2]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform4f((int) uLoc, value[0], value[1], value[2], value[3]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformI(GLES3XProgram program, long uLoc, int[] value) {
        if (GLES.getCapabilities().GLES31) {
            switch (value.length) {
                case 1:
                    GLES31.glProgramUniform1i(program.programId, (int) uLoc, value[0]);
                    break;
                case 2:
                    GLES31.glProgramUniform2i(program.programId, (int) uLoc, value[0], value[1]);
                    break;
                case 3:
                    GLES31.glProgramUniform3i(program.programId, (int) uLoc, value[0], value[1], value[2]);
                    break;
                case 4:
                    GLES31.glProgramUniform4i(program.programId, (int) uLoc, value[0], value[1], value[2], value[3]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (value.length) {
                case 1:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform1i((int) uLoc, value[0]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 2:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform2i((int) uLoc, value[0], value[1]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 3:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform3i((int) uLoc, value[0], value[1], value[2]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniform4i((int) uLoc, value[0], value[1], value[2], value[3]);
                    GLES20.glUseProgram(currentProgram);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
            }
        }
    }

    @Override
    public void programSetUniformMatD(GLES3XProgram program, long uLoc, DoubleBuffer mat) {
        throw new UnsupportedOperationException("64bit uniforms are not supported!");
    }

    @Override
    public void programSetUniformMatF(GLES3XProgram program, long uLoc, FloatBuffer mat) {
        if (GLES.getCapabilities().GLES31) {
            switch (mat.limit()) {
                case 4:
                    GLES31.glProgramUniformMatrix2fv(program.programId, (int) uLoc, false, mat);
                    break;
                case 9:
                    GLES31.glProgramUniformMatrix3fv(program.programId, (int) uLoc, false, mat);
                    break;
                case 16:
                    GLES31.glProgramUniformMatrix4fv(program.programId, (int) uLoc, false, mat);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
            }
        } else {
            final int currentProgram = GLES20.glGetInteger(GLES20.GL_CURRENT_PROGRAM);

            switch (mat.limit()) {
                case 4:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix2fv((int) uLoc, false, mat);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 9:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix3fv((int) uLoc, false, mat);
                    GLES20.glUseProgram(currentProgram);
                    break;
                case 16:
                    GLES20.glUseProgram(program.programId);
                    GLES20.glUniformMatrix4fv((int) uLoc, false, mat);
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
    public void samplerBind(long unit, GLES3XSampler sampler) {
        GLES30.glBindSampler((int) unit, sampler.samplerId);
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
    public void samplerSetParameter(GLES3XSampler sampler, long param, long value) {
        GLES30.glSamplerParameteri(sampler.samplerId, (int) param, (int) value);
    }

    @Override
    public void samplerSetParameter(GLES3XSampler sampler, long param, double value) {
        GLES30.glSamplerParameterf(sampler.samplerId, (int) param, (float) value);
    }

    @Override
    public void scissorTestDisable() {
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(long left, long bottom, long width, long height) {
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor((int) left, (int) bottom, (int) width, (int) height);
    }

    @Override
    public GLES3XShader shaderCompile(long type, String source) {
        final GLES3XShader shader = new GLES3XShader();

        shader.shaderId = GLES20.glCreateShader((int) type);
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
    public long shaderGetParameter(GLES3XShader shader, long pName) {
        return GLES20.glGetShaderi(shader.shaderId, (int) pName);
    }

    @Override
    public GLES3XTexture textureAllocate(long mipmaps, long internalFormat, long width, long height, long depth) {
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
        texture.internalFormat = (int) internalFormat;

        int currentTexture;
        switch (target) {
            case GLES20.GL_TEXTURE_2D:
                currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAX_LEVEL, (int) mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, i, (int) internalFormat, (int) width, (int) height, 0, guessFormat((int) internalFormat), GLES20.GL_UNSIGNED_BYTE, 0);
                    width = Math.max(1, (width / 2));
                    height = Math.max(1, (height / 2));
                }

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
                break;
            case GLES30.GL_TEXTURE_3D:
                currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_BASE_LEVEL, 0);
                GLES20.glTexParameteri(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MAX_LEVEL, (int) mipmaps);

                for (int i = 0; i < mipmaps; i++) {
                    GLES30.glTexImage3D(GLES30.GL_TEXTURE_3D, i, (int) internalFormat, (int) width, (int) height, (int) depth, 0, guessFormat((int) internalFormat), GLES20.GL_UNSIGNED_BYTE, 0);
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
    public void textureAllocatePage(GLES3XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public void textureBind(GLES3XTexture texture, long unit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (int) unit);
        GLES20.glBindTexture(texture.target, texture.textureId);
    }

    @Override
    public void textureDeallocatePage(GLES3XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
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
    public void textureGetData(GLES3XTexture texture, long level, long format, long type, ByteBuffer out) {
        throw new UnsupportedOperationException("textureGetData is not supported!");
    }

    @Override
    public double textureGetMaxAnisotropy() {
        if (GLES.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            return GLES20.glGetInteger(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        } else {
            return 1.0;
        }
    }

    @Override
    public long textureGetMaxBoundTextures() {
        return GLES20.glGetInteger(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @Override
    public long textureGetMaxSize() {
        return GLES20.glGetInteger(GLES20.GL_MAX_TEXTURE_SIZE);
    }

    @Override
    public long textureGetPageDepth(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public long textureGetPageHeight(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public long textureGetPageWidth(GLES3XTexture texture) {
        throw new UnsupportedOperationException("Sparse textures are not supported!");
    }

    @Override
    public long textureGetPreferredFormat(long internalFormat) {
        return GLES20.GL_RGBA;
    }

    @Override
    public void textureInvalidateData(GLES3XTexture texture, long level) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public void textureInvalidateRange(GLES3XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth) {
        throw new UnsupportedOperationException("Invalidate subdata is not supported!");
    }

    @Override
    public void textureSetData(GLES3XTexture texture, long level, long xOffset, long yOffset, long zOffset, long width, long height, long depth, long format, long type, ByteBuffer data) {
        switch (texture.target) {
            case GLES20.GL_TEXTURE_2D: {
                final int currentTexture = GLES20.glGetInteger(GLES20.GL_TEXTURE_BINDING_2D);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);
                GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, (int) level, (int) xOffset, (int) yOffset, (int) width, (int) height, (int) format, (int) type, data);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, currentTexture);
            }
            break;
            case GLES30.GL_TEXTURE_3D: {
                final int currentTexture = GLES20.glGetInteger(GLES30.GL_TEXTURE_BINDING_3D);

                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, texture.textureId);
                GLES30.glTexSubImage3D(GLES30.GL_TEXTURE_3D, (int) level, (int) xOffset, (int) yOffset, (int) zOffset, (int) width, (int) height, (int) depth, (int) format, (int) type, data);
                GLES20.glBindTexture(GLES30.GL_TEXTURE_3D, currentTexture);
            }
            break;

        }
    }

    @Override
    public void textureSetParameter(GLES3XTexture texture, long param, long value) {
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
        GLES20.glTexParameteri(texture.target, (int) param, (int) value);
        GLES20.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void textureSetParameter(GLES3XTexture texture, long param, double value) {
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
        GLES20.glTexParameterf(texture.target, (int) param, (float) value);
        GLES20.glBindTexture(texture.target, currentTexture);
    }

    @Override
    public void vertexArrayAttachBuffer(GLES3XVertexArray vao, long index, GLES3XBuffer buffer, long size, long type, long stride, long offset, long divisor) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
        GLES20.glEnableVertexAttribArray((int) index);

        GLES20.glVertexAttribPointer((int) index, (int) size, (int) type, false, (int) stride, offset);

        if (divisor > 0) {
            GLES30.glVertexAttribDivisor((int) index, (int) divisor);
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
    public void vertexArrayDrawArrays(GLES3XVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawArrays((int) drawMode, (int) start, (int) count);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, long drawMode, long offset) {
        if (GLES.getCapabilities().GLES31) {
            final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
            final int currentIndirect = GLES20.glGetInteger(GLES31.GL_DRAW_INDIRECT_BUFFER_BINDING);

            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawArraysIndirect((int) drawMode, offset);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
            GLES30.glBindVertexArray(currentVao);
        } else {
            throw new UnsupportedOperationException("Draw Arrays Indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GLES3XVertexArray vao, long drawMode, long first, long count, long instanceCount) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES30.glDrawArraysInstanced((int) drawMode, (int) first, (int) count, (int) instanceCount);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElements(GLES3XVertexArray vao, long drawMode, long count, long type, long offset) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glDrawElements((int) drawMode, (int) count, (int) type, offset);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GLES3XVertexArray vao, GLES3XBuffer cmdBuffer, long drawMode, long indexType, long offset) {
        if (GLES.getCapabilities().GLES31) {
            final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);
            final int currentIndirect = GLES20.glGetInteger(GLES31.GL_DRAW_INDIRECT_BUFFER_BINDING);

            GLES30.glBindVertexArray(vao.vertexArrayId);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, cmdBuffer.bufferId);
            GLES31.glDrawElementsIndirect((int) drawMode, (int) indexType, offset);
            GLES20.glBindBuffer(GLES31.GL_DRAW_INDIRECT_BUFFER, currentIndirect);
            GLES30.glBindVertexArray(currentVao);
        } else {
            throw new UnsupportedOperationException("Draw Elements Indirect is not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GLES3XVertexArray vao, long drawMode, long count, long type, long offset, long instanceCount) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES30.glDrawElementsInstanced((int) drawMode, (int) count, (int) type, offset, (int) instanceCount);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayDrawTransformFeedback(GLES3XVertexArray vao, long drawMode, long start, long count) {
        final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

        GLES30.glBindVertexArray(vao.vertexArrayId);
        GLES20.glEnable(GLES30.GL_RASTERIZER_DISCARD);
        GLES30.glBeginTransformFeedback((int) drawMode);
        GLES20.glDrawArrays((int) drawMode, (int) start, (int) count);
        GLES30.glEndTransformFeedback();
        GLES20.glDisable(GLES30.GL_RASTERIZER_DISCARD);
        GLES30.glBindVertexArray(currentVao);
    }

    @Override
    public void vertexArrayMultiDrawArrays(GLES3XVertexArray vao, long drawMode, IntBuffer first, IntBuffer count) {
        if (GLES.getCapabilities().GL_EXT_multi_draw_arrays) {
            final int currentVao = GLES20.glGetInteger(GLES30.GL_VERTEX_ARRAY_BINDING);

            GLES30.glBindVertexArray(vao.vertexArrayId);
            EXTMultiDrawArrays.glMultiDrawArraysEXT((int) drawMode, first, count);
            GLES30.glBindVertexArray(currentVao);
        } else {
            throw new UnsupportedOperationException("Multi Draw Arrays is not supported!");
        }
    }

    @Override
    public void viewportApply(long x, long y, long width, long height) {
        GLES20.glViewport((int) x, (int) y, (int) width, (int) height);
    }

}
