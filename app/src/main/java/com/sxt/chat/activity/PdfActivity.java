package com.sxt.chat.activity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.sxt.chat.R;
import com.sxt.chat.adapter.PdfAdapter;
import com.sxt.chat.base.HeaderActivity;
import com.sxt.chat.view.CustomRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/12/21.
 */
public class PdfActivity extends HeaderActivity {

    private List<Bitmap> pdfBitmaps = new ArrayList<>();
    private CustomRecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        recyclerView = findViewById(R.id.recyclerView);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                String path = getExternalCacheDir() + File.separator + "hard.pdf";
                ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_WRITE);
                PdfRenderer renderer = new PdfRenderer(input);
                final int pageCount = renderer.getPageCount();
                Bitmap mBitmap;
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    //将当前页的内容渲染到bitmap中
                    page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    //存储当前的bitmap
                    pdfBitmaps.add(mBitmap);
                    //释放当前页
                    page.close();
                }
                //释放渲染器
                renderer.close();
                input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setAdapter(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false), new PdfAdapter(this, pdfBitmaps));
    }
}
