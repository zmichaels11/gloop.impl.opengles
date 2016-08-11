/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.gloop.impl.gles;

import java.nio.ByteBuffer;

/**
 *
 * @author zmichaels
 */
public final class CommonUtils {

    private CommonUtils() {
    }

    public static ByteBuffer rbPixelSwap(final ByteBuffer dst, final ByteBuffer src) {
        final int size = dst.capacity();

        assert size == src.capacity();

        for (int i = 0; i < size; i += 4) {
            final byte b = src.get(i);
            final byte g = src.get(i + 1);
            final byte r = src.get(i + 2);
            final byte a = src.get(i + 3);

            dst.put(i, r);
            dst.put(i + 1, g);
            dst.put(i + 2, b);
            dst.put(i + 3, a);
        }

        return dst;
    }

}
