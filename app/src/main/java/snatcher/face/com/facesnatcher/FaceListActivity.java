package snatcher.face.com.facesnatcher;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.app.BadRequestException;
import com.kii.cloud.storage.exception.app.ConflictException;
import com.kii.cloud.storage.exception.app.ForbiddenException;
import com.kii.cloud.storage.exception.app.NotFoundException;
import com.kii.cloud.storage.exception.app.UnauthorizedException;
import com.kii.cloud.storage.exception.app.UndefinedException;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FaceListActivity extends Activity {

    private ImageView mFaceImageView;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_list);

        mFaceImageView = (ImageView) findViewById(R.id.imageView2);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        FaceAdapter faceAdapter = new FaceAdapter(this);
        gridView.setAdapter(faceAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.hue_imageview);
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                mFaceImageView.startAnimation(myFadeInAnimation);
                mFaceImageView.setImageDrawable(imageView.getDrawable());
            }
        });
    }

    public class FaceAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private List<KiiObject> mObjLists = new ArrayList<KiiObject>();

        class ViewHolder {
            public ImageView hueImageView;
            public TextView hueTextView;
        }

        FaceAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);

            final KiiQuery all_query = new KiiQuery();

            KiiUser.loginWithToken(new KiiUserCallBack() {
                @Override
                public void onLoginCompleted(final int token, final KiiUser user, final Exception exception) {
                    super.onLoginCompleted(token, user, exception);
                    Kii.bucket("tutorial").query(new KiiQueryCallBack<KiiObject>() {
                        @Override
                        public void onQueryCompleted(final int token,
                                                     final KiiQueryResult<KiiObject> result,
                                                     final Exception exception) {
                            mObjLists = result.getResult();

                            Toast.makeText(getApplicationContext(), "Kii Query Complete",
                                    Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }, all_query);
                }
            }, AppConstants.ACCESS_TOKEN);
        }


        public int getCount() {
            return mObjLists.size();
        }

        public KiiObject getItem(int position) {
            return mObjLists.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, final View convertView, final ViewGroup parent) {

            View view = mLayoutInflater.inflate(R.layout.grid_item_hue, null);
            final ImageView hueImageView = (ImageView) view.findViewById(R.id.hue_imageview);
            final TextView hueTextView = (TextView) view.findViewById(R.id.hue_textview);

            new Thread(new Runnable() {
                public void run() {
                    // Assume that KiiObject object; is already set.
                    int time = 24 * 60 * 60; // 1 hour x 24 = 1day
                    final String publishUrl;
                    try {
                        publishUrl = mObjLists.get(position).publishBodyExpiresIn(time);
                        mHandler.post(new Runnable() {
                            public void run() {
                                Picasso.with(mContext).load(publishUrl).into(hueImageView);
                                //hueTextView.setText(mObjLists.get(position).getString("mode"));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadRequestException e) {
                        e.printStackTrace();
                    } catch (UnauthorizedException e) {
                        e.printStackTrace();
                    } catch (ForbiddenException e) {
                        e.printStackTrace();
                    } catch (ConflictException e) {
                        e.printStackTrace();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (UndefinedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            return view;
        }
    }

}
