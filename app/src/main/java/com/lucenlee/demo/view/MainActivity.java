package com.lucenlee.demo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    Integer[] mRes = {
            R.drawable.image_1,
            R.drawable.image_2,
            R.drawable.image_3,
            R.drawable.image_4,
            R.drawable.image_5,
            R.drawable.image_6,
            R.drawable.image_8,
            R.drawable.image_9
    };
    private MyCardAdapter mMyCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.activity_main_rl);
        mMyCardAdapter = new MyCardAdapter(this, Arrays.asList(mRes));
        mRecyclerView.setLayoutManager(new CardLayoutManager(0.9f, 0.2f, 0));
        mRecyclerView.setAdapter(mMyCardAdapter);
        mMyCardAdapter.notifyDataSetChanged();


    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_btn_1:
                Random random = new Random();
                int i = random.nextInt(mMyCardAdapter.getItemCount());
                Log.e("111", "onClick: "+i);
                mRecyclerView.smoothScrollToPosition(i);
                break;
            case R.id.activity_main_btn_2:

                break;
        }

    }


    public static class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.MyViewHolder> {

        Context mContext;
        List<Integer> mIntegers;

        public MyCardAdapter(Context context, List<Integer> integers) {
            mContext = context;
            mIntegers = integers;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder,final int i) {
            viewHolder.mImageView.setImageResource(mIntegers.get(i));
            viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "" + i, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mIntegers != null) return mIntegers.size();
            return 0;
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.item_iv);
            }
        }
    }
}
