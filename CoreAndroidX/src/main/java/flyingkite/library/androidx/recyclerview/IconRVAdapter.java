package flyingkite.library.androidx.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.flyingkite.library.R;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconRVAdapter extends RVAdapter<Integer, IconRVAdapter.IconVH, IconRVAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<Integer, IconVH> {
        //void onClick(String name, IconVH vh, int position);
    }

    @LayoutRes
    protected int holderLayout() {
        return R.layout.view_square_image;
    }

    @IdRes
    protected int itemId() {
        return R.id.itemIcon;
    }

    @NonNull
    @Override
    public IconVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconVH(inflateView(parent, holderLayout()));
    }

    @Override
    public void onBindViewHolder(@NonNull IconVH vh, int position) {
        super.onBindViewHolder(vh, position);
        Context c = vh.itemView.getContext();
        int cid = itemOf(position);
        vh.icon.setImageResource(cid);
        // This is string part
        //String cid = itemOf(position);
//        Glide.with(c).load(cid)
//                .apply(RequestOptions.centerCropTransform().placeholder(R.drawable.unknown_card))
//                .into(vh.icon);
    }

    public class IconVH extends RecyclerView.ViewHolder {
        private ImageView icon;

        public IconVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(itemId());
        }
    }
}

