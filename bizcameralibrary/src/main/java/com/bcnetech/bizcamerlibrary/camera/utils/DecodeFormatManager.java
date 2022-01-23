package com.bcnetech.bizcamerlibrary.camera.utils;

import com.google.zxing.BarcodeFormat;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by oujianfeng on 2017/4/25.
 */

public final class DecodeFormatManager {
    static final Set<BarcodeFormat> ONE_D_FORMATS;

    static final Set<BarcodeFormat> PRODUCT_FORMATS;

    static final Set<BarcodeFormat> INDUSTRIAL_FORMATS;

    static final Set<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
    static final Set<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);

    static{
        PRODUCT_FORMATS = EnumSet.of(BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED);

        INDUSTRIAL_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.ITF,
                BarcodeFormat.CODABAR);

        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS);
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS);
    }
}
