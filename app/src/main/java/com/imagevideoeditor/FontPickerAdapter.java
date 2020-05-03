package com.imagevideoeditor;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FontPickerAdapter extends RecyclerView.Adapter<FontPickerAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> fontPickerFonts;
    private List<Integer> fontId;
    private int selectedPos = 0;
    private OnFontSelectListner onFontSelectListner;

    FontPickerAdapter(@NonNull Context context, int selectedPos, @NonNull List<String> fontPickerFonts, List<Integer> fontIds) {
        this.context = context;
        this.selectedPos = selectedPos;
        this.inflater = LayoutInflater.from(context);
        this.fontId = fontIds;
        this.fontPickerFonts = fontPickerFonts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_font_select, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.name.setText(fontPickerFonts.get(i));
        Typeface typeface = ResourcesCompat.getFont(context, fontId.get(i));
        viewHolder.name.setTypeface(typeface);
        if (selectedPos == i) {
            viewHolder.name.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            viewHolder.name.setBackgroundColor(ContextCompat.getColor(context, R.color.black_trasp));
        }
    }

    @Override
    public int getItemCount() {
        return fontPickerFonts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvFontName);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getAdapterPosition() != selectedPos) {
                        if (onFontSelectListner != null) {
                            selectedPos = getAdapterPosition();
                            name.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            onFontSelectListner.onFontSelcetion(getAdapterPosition());
                            notifyDataSetChanged();
                        }
                    }

                }
            });
        }
    }

    public int getSelecetedPosition(){
        return selectedPos;
    }


    public void setOnFontSelectListener(OnFontSelectListner onFontSelectListner) {
        this.onFontSelectListner = onFontSelectListner;
    }

    interface OnFontSelectListner {
        void onFontSelcetion(int position);
    }


}
