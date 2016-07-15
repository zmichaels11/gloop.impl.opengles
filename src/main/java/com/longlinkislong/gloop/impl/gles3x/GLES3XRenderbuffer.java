/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.glspi.Renderbuffer;

/**
 *
 * @author zmichaels
 */
public class GLES3XRenderbuffer implements Renderbuffer {
    int renderbufferId = -1;

    @Override
    public boolean isValid() {
        return renderbufferId != -1;
    }

}
