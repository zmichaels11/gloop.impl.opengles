/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles2x;

import com.longlinkislong.gloop.glspi.Driver;
import com.longlinkislong.gloop.glspi.Shader;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengles.ANGLEFramebufferBlit;
import org.lwjgl.opengles.ANGLEInstancedArrays;
import org.lwjgl.opengles.EXTDrawBuffers;
import org.lwjgl.opengles.EXTInstancedArrays;
import org.lwjgl.opengles.EXTMapBufferRange;
import org.lwjgl.opengles.EXTTextureFilterAnisotropic;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.opengles.OESMapbuffer;
import org.lwjgl.opengles.OESVertexArrayObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
final class GLES2XDriver implements Driver<GLES2XBuffer, GLES2XFramebuffer, GLES2XRenderbuffer, GLES2XTexture, GLES2XShader, GLES2XProgram, GLES2XSampler, GLES2XVertexArray, GLES2XDrawQuery> {

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
    public void bufferAllocate(GLES2XBuffer bt, long size, int usage) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, size, usage);
        bt.size = size;
        bt.usage = usage;
    }

    @Override
    public void bufferAllocateImmutable(GLES2XBuffer bt, long size, int bitflags) {
        bufferAllocate(bt, size, GLES20.GL_DYNAMIC_DRAW);
    }

    @Override
    public void bufferBindAtomic(GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("Atomic buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindAtomic(GLES2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Atomic buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindFeedback(GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("Feedback buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindFeedback(GLES2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Feedback buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindStorage(GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("Storage buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindStorage(GLES2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Storage buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindUniform(GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("Uniform buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferBindUniform(GLES2XBuffer bt, int i, long l, long l1) {
        throw new UnsupportedOperationException("Uniform buffers are not supported in OpenGLES 2.0!");
    }

    @Override
    public void bufferCopyData(GLES2XBuffer srcBuffer, long srcOffset, GLES2XBuffer dstBuffer, long dstOffset, long size) {
        final ByteBuffer src = this.bufferMapData(srcBuffer, srcOffset, size, EXTMapBufferRange.GL_MAP_READ_BIT_EXT);
        final ByteBuffer dst = this.bufferMapData(dstBuffer, dstOffset, size, EXTMapBufferRange.GL_MAP_WRITE_BIT_EXT);

        for (int i = 0; i < size; i++) {
            dst.put(i, src.get(i));
        }

        this.bufferUnmapData(dstBuffer);
        this.bufferUnmapData(srcBuffer);
    }

    @Override
    public GLES2XBuffer bufferCreate() {
        final GLES2XBuffer buffer = new GLES2XBuffer();

        buffer.bufferId = GLES20.glGenBuffers();
        return buffer;
    }

    @Override
    public void bufferDelete(GLES2XBuffer bt) {
        if (bt.isValid()) {
            GLES20.glDeleteBuffers(bt.bufferId);
            bt.bufferId = -1;
        }
    }

    @Override
    public void bufferGetData(GLES2XBuffer bt, long offset, ByteBuffer bb) {
        final ByteBuffer data = this.bufferMapData(bt, offset, bb.capacity(), EXTMapBufferRange.GL_MAP_READ_BIT_EXT);

        bb.put(data);

        this.bufferUnmapData(bt);
    }

    @Override
    public int bufferGetMaxUniformBindings() {
        return GLES20.glGetInteger(GLES20.GL_MAX_FRAGMENT_UNIFORM_VECTORS);
    }

    @Override
    public int bufferGetMaxUniformBlockSize() {
        throw new UnsupportedOperationException("Uniform blocks are not supported in OpenGLES2.0!");
    }

    @Override
    public int bufferGetParameterI(GLES2XBuffer bt, int pName) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);
        return GLES20.glGetBufferParameteri(GLES20.GL_ARRAY_BUFFER, pName);
    }

    @Override
    public void bufferInvalidateData(GLES2XBuffer bt) {
        bufferAllocate(bt, bt.size, bt.usage);
    }

    @Override
    public void bufferInvalidateRange(GLES2XBuffer bt, long l, long l1) {
        bufferAllocate(bt, bt.size, bt.usage);
    }

    @Override
    public ByteBuffer bufferMapData(GLES2XBuffer bt, long offset, long length, int flags) {
        final GLESCapabilities caps = GLES.getCapabilities();

        if (caps.GL_EXT_map_buffer_range) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);
            bt.mapBuffer = EXTMapBufferRange.glMapBufferRangeEXT(GLES20.GL_ARRAY_BUFFER, offset, length, flags, bt.mapBuffer);
            return bt.mapBuffer;
        } else if (caps.GL_OES_mapbuffer) {
            if ((flags & EXTMapBufferRange.GL_MAP_READ_BIT_EXT) > 0) {
                throw new UnsupportedOperationException("OES_mapbuffer only supported write only!");
            }

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);

            bt.mapBuffer = OESMapbuffer.glMapBufferOES(GLES20.GL_ARRAY_BUFFER, OESMapbuffer.GL_WRITE_ONLY_OES, length, bt.mapBuffer);
            bt.mapBuffer.position((int) offset);

            return bt.mapBuffer;
        } else {
            throw new UnsupportedOperationException("Context does not supporte EXT_map_buffer_range nor OES_mapbuffer!");
        }
    }

    @Override
    public void bufferSetData(GLES2XBuffer bt, ByteBuffer bb, int usage) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bb, usage);
    }

    @Override
    public void bufferUnmapData(GLES2XBuffer bt) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bt.bufferId);
        OESMapbuffer.glUnmapBufferOES(GLES20.GL_ARRAY_BUFFER);
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
    public void drawQueryBeginConditionalRender(GLES2XDrawQuery queryt, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GLES2XDrawQuery drawQueryCreate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDelete(GLES2XDrawQuery queryt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryDisable(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEnable(int i, GLES2XDrawQuery queryt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void drawQueryEndConditionRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void framebufferAddAttachment(GLES2XFramebuffer ft, int attachmentId, GLES2XTexture texId, int mipmapLevel) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);

        switch (texId.target) {
            case GLES20.GL_TEXTURE_2D:
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachmentId, GLES20.GL_TEXTURE_2D, texId.textureId, mipmapLevel);
                break;
            default:
                throw new UnsupportedOperationException("OpenGLES 2.0 only supports 2D texture targets as Framebuffer attachments!");
        }
    }

    @Override
    public void framebufferAddRenderbuffer(GLES2XFramebuffer ft, int attachmentId, GLES2XRenderbuffer rt) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, attachmentId, GLES20.GL_RENDERBUFFER, rt.renderbufferId);
    }

    @Override
    public void framebufferBind(GLES2XFramebuffer ft, IntBuffer attachments) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);

        if (attachments != null) {
            if (GLES.getCapabilities().GL_EXT_draw_buffers) {
                EXTDrawBuffers.glDrawBuffersEXT(attachments);
            } else {
                throw new UnsupportedOperationException("EXT_draw_buffers is not supported!");
            }
        }
    }

    @Override
    public void framebufferBlit(GLES2XFramebuffer srcFb, int srcX0, int srcY0, int srcX1, int srcY1, GLES2XFramebuffer dstFb, int dstX0, int dstY0, int dstX1, int dstY1, int bitfield, int filter) {
        if (GLES.getCapabilities().GL_ANGLE_framebuffer_blit) {
            GLES20.glBindFramebuffer(ANGLEFramebufferBlit.GL_READ_FRAMEBUFFER_ANGLE, srcFb.framebufferId);
            GLES20.glBindFramebuffer(ANGLEFramebufferBlit.GL_DRAW_FRAMEBUFFER_ANGLE, dstFb.framebufferId);

            ANGLEFramebufferBlit.glBlitFramebufferANGLE(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bitfield, filter);
        } else {
            throw new UnsupportedOperationException("ANGLEFramebufferBlit is not supported!");
        }
    }

    @Override
    public GLES2XFramebuffer framebufferCreate() {
        final GLES2XFramebuffer fb = new GLES2XFramebuffer();
        fb.framebufferId = GLES20.glGenFramebuffers();
        return fb;
    }

    @Override
    public void framebufferDelete(GLES2XFramebuffer ft) {
        if (ft.isValid()) {
            GLES20.glDeleteFramebuffers(ft.framebufferId);
            ft.framebufferId = -1;
        }
    }

    @Override
    public GLES2XFramebuffer framebufferGetDefault() {
        final GLES2XFramebuffer fb = new GLES2XFramebuffer();
        fb.framebufferId = 0;
        return fb;
    }

    @Override
    public void framebufferGetPixels(GLES2XFramebuffer ft, int x, int y, int width, int height, int format, int type, GLES2XBuffer dstBuffer) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support reading a framebuffer into a buffer!");
    }

    @Override
    public void framebufferGetPixels(GLES2XFramebuffer ft, int x, int y, int width, int height, int format, int type, ByteBuffer out) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);
        GLES20.glReadPixels(x, y, width, height, format, type, out);
    }

    @Override
    public boolean framebufferIsComplete(GLES2XFramebuffer ft) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ft.framebufferId);

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

        GLES20.glPolygonOffset(GLES20.GL_FRONT_AND_BACK, polygonMode);
        GLES20.glPolygonOffset(offsetFactor, offsetUnits);
    }

    @Override
    public GLES2XProgram programCreate() {
        final GLES2XProgram program = new GLES2XProgram();

        program.programId = GLES20.glCreateProgram();
        return program;
    }

    @Override
    public void programDelete(GLES2XProgram pt) {
        if (pt.isValid()) {
            GLES20.glDeleteProgram(pt.programId);
            pt.programId = -1;
        }
    }

    @Override
    public void programDispatchCompute(GLES2XProgram pt, int i, int i1, int i2) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not supporte compute shaders!");
    }

    @Override
    public int programGetStorageBlockBinding(GLES2XProgram pt, String string) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support shader storage!");
    }

    @Override
    public int programGetUniformBlockBinding(GLES2XProgram pt, String string) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support uniform buffers!");
    }

    @Override
    public int programGetUniformLocation(GLES2XProgram pt, String name) {
        GLES20.glUseProgram(pt.programId);
        return GLES20.glGetUniformLocation(pt.programId, name);
    }

    @Override
    public void programLinkShaders(GLES2XProgram pt, Shader[] shaders) {
        for (Shader shader : shaders) {
            GLES20.glAttachShader(pt.programId, ((GLES2XShader) shader).shaderId);
        }

        GLES20.glLinkProgram(pt.programId);

        for (Shader shader : shaders) {
            GLES20.glDetachShader(pt.programId, ((GLES2XShader) shader).shaderId);
        }
    }

    @Override
    public void programSetAttribLocation(GLES2XProgram pt, int index, String name) {
        GLES20.glBindAttribLocation(pt.programId, index, name);
    }

    @Override
    public void programSetFeedbackBuffer(GLES2XProgram pt, int i, GLES2XBuffer bt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support feedback buffers!");
    }

    @Override
    public void programSetFeedbackVaryings(GLES2XProgram pt, String[] strings) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support feedback buffers!");
    }

    @Override
    public void programSetStorage(GLES2XProgram pt, String string, GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support shader storage!");
    }

    @Override
    public void programSetStorageBlockBinding(GLES2XProgram pt, String string, int i) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support shader storage!");
    }

    @Override
    public void programSetUniformBlock(GLES2XProgram pt, String string, GLES2XBuffer bt, int i) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support uniform buffers!");
    }

    @Override
    public void programSetUniformBlockBinding(GLES2XProgram pt, String string, int i) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support uniform buffers!");
    }

    @Override
    public void programSetUniformD(GLES2XProgram pt, int i, double[] doubles) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support 64bit uniforms!");
    }

    @Override
    public void programSetUniformF(GLES2XProgram pt, int uLoc, float[] value) {
        GLES20.glUseProgram(pt.programId);

        switch (value.length) {
            case 1:
                GLES20.glUniform1f(uLoc, value[0]);
                break;
            case 2:
                GLES20.glUniform2f(uLoc, value[0], value[1]);
                break;
            case 3:
                GLES20.glUniform3f(uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                GLES20.glUniform4f(uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformI(GLES2XProgram pt, int uLoc, int[] value) {
        GLES20.glUseProgram(pt.programId);

        switch (value.length) {
            case 1:
                GLES20.glUniform1i(uLoc, value[0]);
                break;
            case 2:
                GLES20.glUniform2i(uLoc, value[0], value[1]);
                break;
            case 3:
                GLES20.glUniform3i(uLoc, value[0], value[1], value[2]);
                break;
            case 4:
                GLES20.glUniform4i(uLoc, value[0], value[1], value[2], value[3]);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uniform vector size: " + value.length);
        }
    }

    @Override
    public void programSetUniformMatD(GLES2XProgram pt, int i, DoubleBuffer db) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support 64bit uniforms!");
    }

    @Override
    public void programSetUniformMatF(GLES2XProgram pt, int uLoc, FloatBuffer mat) {
        GLES20.glUseProgram(pt.programId);

        switch (mat.remaining()) {
            case 4:
                GLES20.glUniformMatrix2fv(uLoc, false, mat);
                break;
            case 9:
                GLES20.glUniformMatrix3fv(uLoc, false, mat);
                break;
            case 16:
                GLES20.glUniformMatrix4fv(uLoc, false, mat);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported matrix size: " + mat.limit());
        }
    }

    @Override
    public void programUse(GLES2XProgram pt) {
        GLES20.glUseProgram(pt.programId);
    }

    @Override
    public GLES2XRenderbuffer renderbufferCreate(int internalFormat, int width, int height) {
        // adapt common OpenGL formats to similar OpenGLES formats
        switch (internalFormat) {
            case GL11.GL_RGB8:
                internalFormat = GLES20.GL_RGB565;
                break;
            case GL11.GL_RGBA8:
                internalFormat = GLES20.GL_RGBA4;
                break;
        }

        final GLES2XRenderbuffer renderbuffer = new GLES2XRenderbuffer();

        renderbuffer.renderbufferId = GLES20.glGenRenderbuffers();

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderbuffer.renderbufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, simplifyFormat(internalFormat), width, height);

        return renderbuffer;
    }

    @Override
    public void renderbufferDelete(GLES2XRenderbuffer rt) {
        if (rt.isValid()) {
            GLES20.glDeleteRenderbuffers(rt.renderbufferId);
            rt.renderbufferId = -1;
        }
    }

    @Override
    public void samplerBind(int i, GLES2XSampler st) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support Sampler objects!");
    }

    @Override
    public GLES2XSampler samplerCreate() {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support Sampler objects!");
    }

    @Override
    public void samplerDelete(GLES2XSampler st) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support Sampler objects!");
    }

    @Override
    public void samplerSetParameter(GLES2XSampler st, int i, int i1) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support Sampler objects!");
    }

    @Override
    public void samplerSetParameter(GLES2XSampler st, int i, float f) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support Sampler objects!");
    }

    @Override
    public void scissorTestDisable() {
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }

    @Override
    public void scissorTestEnable(int x, int y, int width, int height) {
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(x, y, width, height);
    }

    @Override
    public GLES2XShader shaderCompile(int type, String source) {
        final GLES2XShader shader = new GLES2XShader();

        shader.shaderId = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader.shaderId, source);
        GLES20.glCompileShader(shader.shaderId);

        return shader;
    }

    @Override
    public void shaderDelete(GLES2XShader st) {
        if (st.isValid()) {
            GLES20.glDeleteShader(st.shaderId);
            st.shaderId = -1;
        }
    }

    @Override
    public String shaderGetInfoLog(GLES2XShader st) {
        return GLES20.glGetShaderInfoLog(st.shaderId);
    }

    @Override
    public int shaderGetParameterI(GLES2XShader st, int pName) {
        return GLES20.glGetShaderi(st.shaderId, pName);
    }

    @Override
    public int shaderGetVersion() {
        return 100;
    }

    @Override
    public GLES2XTexture textureAllocate(int mipmaps, int internalFormat, int width, int height, int depth, int dataType) {
        if (depth != 1) {
            throw new IllegalArgumentException("OpenGLES2.0 only supports 2D textures!");
        }

        internalFormat = simplifyFormat(internalFormat);

        final GLES2XTexture texture = new GLES2XTexture();

        texture.textureId = GLES20.glGenTextures();
        texture.target = GLES20.GL_TEXTURE_2D;
        texture.internalFormat = internalFormat;

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.textureId);

        for (int i = 0; i < mipmaps; i++) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, i, internalFormat, width, height, 0, guessFormat(internalFormat), dataType, 0);
            width = Math.max(1, (width / 2));
            height = Math.max(1, (height / 2));
        }

        return texture;
    }

    @Override
    public void textureAllocatePage(GLES2XTexture tt, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support sparse textures!");
    }

    @Override
    public void textureBind(GLES2XTexture tt, int unit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
        GLES20.glBindTexture(tt.target, tt.textureId);
    }

    @Override
    public void textureDeallocatePage(GLES2XTexture tt, int i, int i1, int i2, int i3, int i4, int i5, int i6) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support sparse textures!");
    }

    @Override
    public void textureDelete(GLES2XTexture tt) {
        if (tt.isValid()) {
            GLES20.glDeleteTextures(tt.textureId);
            tt.textureId = -1;
        }
    }

    @Override
    public void textureGenerateMipmap(GLES2XTexture tt) {
        GLES20.glBindTexture(tt.target, tt.textureId);
        GLES20.glGenerateMipmap(tt.target);
    }

    @Override
    public void textureGetData(GLES2XTexture tt, int i, int i1, int i2, ByteBuffer bb) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support retrieving texture pixel data!");
    }

    @Override
    public float textureGetMaxAnisotropy() {
        if (GLES.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            return GLES20.glGetFloat(org.lwjgl.opengles.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        } else {
            return 1F;
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
    public int textureGetPageDepth(GLES2XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support sparse textures!");
    }

    @Override
    public int textureGetPageHeight(GLES2XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support sparse textures!");
    }

    @Override
    public int textureGetPageWidth(GLES2XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support sparse textures!");
    }

    @Override
    public int textureGetPreferredFormat(int internalFormat) {
        return internalFormat;
    }

    @Override
    public void textureInvalidateData(GLES2XTexture tt, int i) {

    }

    @Override
    public void textureInvalidateRange(GLES2XTexture tt, int i, int i1, int i2, int i3, int i4, int i5, int i6) {

    }

    @Override
    public long textureMap(GLES2XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support bindless textures!");
    }

    @Override
    public void textureSetData(GLES2XTexture texture, int level, int xOffset, int yOffset, int zOffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
        GLES20.glBindTexture(texture.target, texture.textureId);

        GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, xOffset, yOffset, width, height, format, type, data);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GLES2XDriver.class);

    @Override
    public void textureSetParameter(GLES2XTexture tt, int pName, int value) {
        switch (pName) {
            case GLES20.GL_TEXTURE_MIN_FILTER:
            case GLES20.GL_TEXTURE_MAG_FILTER:
            case GLES20.GL_TEXTURE_WRAP_S:
            case GLES20.GL_TEXTURE_WRAP_T:
                GLES20.glBindTexture(tt.target, tt.textureId);
                GLES20.glTexParameteri(tt.target, pName, value);
                break;
            default:
                LOGGER.trace("Unsupported texture parameter name: {}", pName);
        }
    }

    @Override
    public void textureSetParameter(GLES2XTexture tt, int pName, float value) {
        switch (pName) {
            case EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT:
                if (!GLES.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                    LOGGER.trace("EXT_texture_filter_anisotropic is not supported!");
                    return;
                }
            case GLES20.GL_TEXTURE_MIN_FILTER:
            case GLES20.GL_TEXTURE_MAG_FILTER:
            case GLES20.GL_TEXTURE_WRAP_S:
            case GLES20.GL_TEXTURE_WRAP_T:
                GLES20.glBindTexture(tt.target, tt.textureId);
                GLES20.glTexParameterf(tt.target, pName, value);
                break;
            default:
                LOGGER.trace("Unsupported texture parameter name: {}", pName);
        }
    }

    @Override
    public void textureUnmap(GLES2XTexture tt) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support bindless textures!");
    }

    @Override
    public void transformFeedbackBegin(int i) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support feedback draw!");
    }

    @Override
    public void transformFeedbackEnd() {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support feedback draw!");
    }

    @Override
    public void vertexArrayAttachBuffer(GLES2XVertexArray vao, int index, GLES2XBuffer buffer, int size, int type, int stride, long offset, int divisor) {
        final GLESCapabilities caps = GLES.createCapabilities();

        if (caps.GL_OES_vertex_array_object) {
            OESVertexArrayObject.glBindVertexArrayOES(vao.vertexArrayId);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
            GLES20.glVertexAttribPointer(index, size, type, false, stride, offset);

            if (divisor != 0) {
                if (caps.GL_ANGLE_instanced_arrays) {
                    ANGLEInstancedArrays.glVertexAttribDivisorANGLE(index, divisor);
                } else {
                    throw new UnsupportedOperationException("ANGLE_instanced_arrays is not supported!");
                }
            }            

            GLES20.glEnableVertexAttribArray(index);
        } else {
            final Runnable bindStatement = () -> {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer.bufferId);
                GLES20.glVertexAttribPointer(index, size, type, false, stride, offset);
                GLES20.glEnableVertexAttribArray(index);
            };

            vao.bindStatements.add(bindStatement);
        }
    }

    @Override
    public void vertexArrayAttachIndexBuffer(GLES2XVertexArray vat, GLES2XBuffer bt) {
        if (GLES.getCapabilities().GL_OES_vertex_array_object) {
            OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bt.bufferId);
        } else {
            final Runnable bindStatement = () -> GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bt.bufferId);

            vat.bindStatements.add(bindStatement);
        }
    }

    @Override
    public GLES2XVertexArray vertexArrayCreate() {
        return new GLES2XVertexArray();
    }

    @Override
    public void vertexArrayDelete(GLES2XVertexArray vat) {
        if (vat.isValid()) {
            if (GLES.getCapabilities().GL_OES_vertex_array_object) {
                OESVertexArrayObject.glDeleteVertexArraysOES(vat.vertexArrayId);
            } else {
                vat.bindStatements.clear();
            }

            vat.vertexArrayId = -1;
        }
    }

    @Override
    public void vertexArrayDrawArrays(GLES2XVertexArray vat, int drawMode, int start, int count) {
        if (GLES.getCapabilities().GL_OES_vertex_array_object) {
            OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
        } else {
            vat.bindStatements.forEach(Runnable::run);
        }

        GLES20.glDrawArrays(drawMode, start, count);
    }

    @Override
    public void vertexArrayDrawArraysIndirect(GLES2XVertexArray vat, GLES2XBuffer bt, int i, long l) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support indirect draw arrays!");
    }

    @Override
    public void vertexArrayDrawArraysInstanced(GLES2XVertexArray vat, int drawMode, int first, int count, int instanceCount) {
        final GLESCapabilities caps = GLES.getCapabilities();

        if (caps.GL_ANGLE_instanced_arrays) {
            if (caps.GL_OES_vertex_array_object) {
                OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
            } else {
                vat.bindStatements.forEach(Runnable::run);
            }

            ANGLEInstancedArrays.glDrawArraysInstancedANGLE(drawMode, first, count, instanceCount);
        } else if (caps.GL_EXT_instanced_arrays) {
            if (caps.GL_OES_vertex_array_object) {
                OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
            } else {
                vat.bindStatements.forEach(Runnable::run);
            }

            EXTInstancedArrays.glDrawArraysInstancedEXT(drawMode, first, count, instanceCount);
        } else {
            throw new UnsupportedOperationException("Neither ANGLE_instanced_arrays nor EXT_instanced_arrays are not supported!");
        }
    }

    @Override
    public void vertexArrayDrawElements(GLES2XVertexArray vat, int drawMode, int count, int type, long offset) {
        if (GLES.getCapabilities().GL_OES_vertex_array_object) {
            OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
        } else {
            vat.bindStatements.forEach(Runnable::run);
        }

        GLES20.glDrawElements(drawMode, count, type, offset);
    }

    @Override
    public void vertexArrayDrawElementsIndirect(GLES2XVertexArray vat, GLES2XBuffer bt, int i, int i1, long l) {
        throw new UnsupportedOperationException("OpenGLES 2.0 does not support indirect draw elements!");
    }

    @Override
    public void vertexArrayDrawElementsInstanced(GLES2XVertexArray vat, int drawMode, int count, int type, long offset, int instanceCount) {
        final GLESCapabilities caps = GLES.getCapabilities();

        if (caps.GL_ANGLE_instanced_arrays) {
            if (caps.GL_OES_vertex_array_object) {
                OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
            } else {
                vat.bindStatements.forEach(Runnable::run);
            }

            ANGLEInstancedArrays.glDrawElementsInstancedANGLE(drawMode, count, type, offset, instanceCount);
        } else if (caps.GL_EXT_instanced_arrays) {
            if (caps.GL_OES_vertex_array_object) {
                OESVertexArrayObject.glBindVertexArrayOES(vat.vertexArrayId);
            } else {
                vat.bindStatements.forEach(Runnable::run);
            }

            EXTInstancedArrays.glDrawElementsInstancedEXT(drawMode, count, type, offset, instanceCount);
        } else {
            throw new UnsupportedOperationException("Neither ANGLE_instanced_arrays nor EXT_instanced_arrays are not supported!");
        }
    }

    @Override
    public void viewportApply(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
    }

    @Override
    public int guessFormat(final int internalFormat) {
        switch (internalFormat) {
            case GLES20.GL_ALPHA:
            case GLES20.GL_LUMINANCE:
            case GLES20.GL_LUMINANCE_ALPHA:
            case GLES20.GL_RGB:
            case GLES20.GL_RGBA:
                return internalFormat;
            default:
                return GLES20.GL_RGBA;
        }
    }

    int simplifyFormat(final int format) {
        switch (format) {
            case GL11.GL_RGB8:
                return GLES20.GL_RGB;
            case GL11.GL_RGBA8:
                return GLES20.GL_RGBA;
            default:
                return format;
        }
    }
}
