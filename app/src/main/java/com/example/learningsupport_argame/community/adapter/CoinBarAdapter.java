package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.learningsupport_argame.databinding.ItemBarBinding;


/**
 * create by zj on 2018/11/5
 */
public class CoinBarAdapter extends  AbsRVAdapter<String,AbsRVAdapter.BindingViewHolder>  {
    private Context context;
    public CoinBarAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBarBinding binding= ItemBarBinding.inflate(mInflater,parent,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        ItemBarBinding binding= (ItemBarBinding) holder.mBinding;
        String str= (String) getItem(position);
        binding.setData(str);
        binding.setPosition(position);

    }


}
