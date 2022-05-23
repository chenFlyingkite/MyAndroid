package flyingkite.library.androidx.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyingkite.library.R;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TextRVAdapter extends RVAdapter<String, TextRVAdapter.TextVH, TextRVAdapter.ItemListener> {
    public interface ItemListener extends RVAdapter.ItemListener<String, TextVH> {
        //void onDelete(String data, TextVH vh, int position);
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
    public TextVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        initCenterScroller(parent);
        return new TextVH(inflateView(parent, holderLayout()));
    }

    @Override
    public void onBindViewHolder(@NonNull TextVH vh, int position) {
        super.onBindViewHolder(vh, position);
        String msg = itemOf(position);
        vh.text.setText(msg);
    }

    public class TextVH extends RecyclerView.ViewHolder {
        private TextView text;

        public TextVH(View v) {
            super(v);
            text = v.findViewById(itemId());
        }
    }
}
