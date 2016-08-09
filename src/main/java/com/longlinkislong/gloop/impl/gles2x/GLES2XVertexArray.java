/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles2x;

import com.longlinkislong.gloop.glspi.VertexArray;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.OESVertexArrayObject;

/**
 *
 * @author zmichaels
 */
final class GLES2XVertexArray implements VertexArray {
    static int LAST_ALLOCATED_SOFT_VAO = 1;
    int vertexArrayId = -1;
    final List<Runnable> bindStatements;

    GLES2XVertexArray() {
        // emulate VAOs 
        if (GLES.getCapabilities().GL_OES_vertex_array_object) {
            this.bindStatements = null;
            this.vertexArrayId = OESVertexArrayObject.glGenVertexArraysOES();
        } else {
            this.bindStatements = new ArrayList<>();
            this.vertexArrayId = LAST_ALLOCATED_SOFT_VAO++;

        }
    }
    @Override
    public boolean isValid() {
        return vertexArrayId != -1;
    }
    
}
