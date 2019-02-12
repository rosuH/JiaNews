package me.rosuh.jianews.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rosuh.android.jianews.R;

/**
 * @author rosu
 * @date 2018/9/30
 */
public class ArticleReadingFrag extends Fragment {
    public static ArticleReadingFrag getInstance() {
        return new ArticleReadingFrag();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_reading_frag, container, false);
    }
}
