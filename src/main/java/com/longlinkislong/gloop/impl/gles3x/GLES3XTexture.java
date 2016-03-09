/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.spi.Texture;

/**
 *
 * @author zmichaels
 */
public final class GLES3XTexture implements Texture {

    int internalFormat;

    int target = -1;

    int textureId = -1;

    @Override
    public boolean isValid() {
        return textureId != -1;
    }

}
