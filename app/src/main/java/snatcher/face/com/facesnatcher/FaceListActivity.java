package snatcher.face.com.facesnatcher;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class FaceListActivity extends Activity {

    private ImageView mFaceImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_list);

        mFaceImageView = (ImageView)findViewById(R.id.imageView2);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new HueAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                ImageView imageView =(ImageView)view.findViewById(R.id.hue_imageview);
                mFaceImageView.setImageDrawable(imageView.getDrawable());
            }
        });

    }

    public class HueAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private String[] mHueArray = {
                "Yumiko", "Kaori", "Jiro", "Tetsu",
                "Yumiko", "Kaori", "Jiro", "Tetsu",
                "Yumiko", "Kaori", "Jiro", "Tetsu",
                "Yumiko", "Kaori", "Jiro", "Tetsu"
        };
        private Integer[] mHueIdArray = {
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2,
                R.drawable.female_face, R.drawable.female_face2
        };

        private class ViewHolder {
            public ImageView hueImageView;
            public TextView hueTextView;
        }

        public HueAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mHueArray.length;
        }

        public Object getItem(int position) {
            return mHueArray[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.grid_item_hue, null);
                holder = new ViewHolder();
                holder.hueImageView = (ImageView) convertView.findViewById(R.id.hue_imageview);
                holder.hueTextView = (TextView) convertView.findViewById(R.id.hue_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.hueImageView.setImageResource(mHueIdArray[position]);
            holder.hueTextView.setText(mHueArray[position]);

            return convertView;
        }
    }

}


