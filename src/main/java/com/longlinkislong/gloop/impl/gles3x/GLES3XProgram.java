/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles3x;

import com.longlinkislong.gloop.glspi.Program;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public final class GLES3XProgram implements Program {
    int programId = -1;
    final Map<String, Integer> uniformBindings = new HashMap<>(0);
    final Map<String, Integer> storageBindings = new HashMap<>(0);
    
    @Override
    public boolean isValid() {
        return programId != -1;
    }
}
