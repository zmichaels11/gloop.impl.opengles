/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles2x;

import com.longlinkislong.gloop.spi.Shader;

/**
 *
 * @author zmichaels
 */
final class GLES2XShader implements Shader {
    int shaderId = -1;
    
    @Override
    public boolean isValid() {
        return shaderId != -1;
    }
}
