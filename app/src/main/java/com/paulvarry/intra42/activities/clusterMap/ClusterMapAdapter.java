package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

public class ClusterMapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CORRIDOR = 0;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_WALL = 2;

    private onLocationClickListener listener;
    private Cluster cluster;
    private ClusterStatus clusterStatus;
    private LayoutInflater li;
    private Context context;

    private int itemPadding1dp;
    private int itemPadding2dp;
    private int itemPadding3dp;
    private float baseItemWidth;
    private float baseItemHeight;

    ClusterMapAdapter(Context context, Cluster cluster, ClusterStatus clusterStatus) {
        this.cluster = cluster;
        this.context = context;
        this.clusterStatus = clusterStatus;

        li = LayoutInflater.from(context);
        itemPadding1dp = (int) Tools.dpToPx(context, 1);
        itemPadding2dp = (int) Tools.dpToPx(context, 2);
        itemPadding3dp = (int) Tools.dpToPx(context, 3);
        baseItemHeight = Tools.dpToPx(context, 42);
        baseItemWidth = Tools.dpToPx(context, 35);
    }

    @Override
    public int getItemViewType(int position) {
        int x = position % cluster.sizeX;
        int y = position / cluster.sizeX;
        Location location = cluster.map[x][y];

        if (location == null || location.kind == null)
            return VIEW_TYPE_CORRIDOR;

        switch (location.kind) {
            case WALL:
                return VIEW_TYPE_WALL;
            case USER:
                return VIEW_TYPE_USER;
            case CORRIDOR:
                return VIEW_TYPE_CORRIDOR;
        }
        return VIEW_TYPE_CORRIDOR;
    }

    public void setOnLocationClickListener(onLocationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_USER:
                return new ViewHolderUser(li, parent);
            case VIEW_TYPE_WALL:
                return new ViewHolderWall(li, parent);
            default:
                return new ViewHolderCorridor(li, parent);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int x = position % cluster.sizeX;
        int y = position / cluster.sizeX;
        if (holder instanceof ViewHolderUser)
            ((ViewHolderUser) holder).bind(cluster.map[x][y]);
        else if (holder instanceof ViewHolderCorridor)
            ((ViewHolderCorridor) holder).bind(cluster.map[x][y]);
        else if (holder instanceof ViewHolderWall)
            ((ViewHolderWall) holder).bind(cluster.map[x][y], x, y);
    }

    @Override
    public int getItemCount() {
        return cluster.sizeX * cluster.sizeY;
    }

    public interface onLocationClickListener {
        void onLocationClicked(Location location);
    }

    class ViewHolderUser extends RecyclerView.ViewHolder {

        private ImageView imageViewContent;
        private Location location;

        ViewHolderUser(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.grid_layout_cluster_map, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onLocationClicked(location);
                }
            });

            imageViewContent = itemView.findViewById(R.id.imageView);
        }

        void bind(Location location) {

            UsersLTE user;
            user = clusterStatus.getUserInLocation(location);
            this.location = location;

            if (location.host == null) {
                imageViewContent.setImageResource(R.drawable.ic_missing_black_25dp);
                imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                imageViewContent.clearColorFilter();
                if (user != null)
                    UserImage.setImageSmall(context, user, imageViewContent);
                else {
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_desktop_mac_black_custom));
                    imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }

            GridLayoutManager.LayoutParams paramsGridLayout = (GridLayoutManager.LayoutParams) imageViewContent.getLayoutParams();
            paramsGridLayout.height = (int) (baseItemHeight);
            paramsGridLayout.width = (int) (baseItemWidth);
            imageViewContent.setPadding(itemPadding1dp, itemPadding1dp, itemPadding1dp, itemPadding1dp);

            if (location.highlight) {

//                paramsGridLayout.height = (int) (baseItemHeight - itemPadding2dp * 2);
//                paramsGridLayout.width = (int) (baseItemWidth - itemPadding2dp * 2);

                imageViewContent.setPadding(itemPadding2dp, itemPadding2dp, itemPadding2dp, itemPadding2dp);

                TypedValue a = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                imageViewContent.setBackgroundResource(a.resourceId);

                imageViewContent.setBackgroundColor(ThemeHelper.getColorAccent(context));
            }
            imageViewContent.setLayoutParams(paramsGridLayout);
        }
    }

    class ViewHolderCorridor extends RecyclerView.ViewHolder {

        ViewHolderCorridor(LayoutInflater inflater, ViewGroup parent) {
            super(new View(context));


        }

        void bind(Location location) {

            GridLayoutManager.LayoutParams paramsGridLayout = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            if (paramsGridLayout == null)
                paramsGridLayout = new GridLayoutManager.LayoutParams(0, 0);
            paramsGridLayout.height = (int) baseItemHeight;
            paramsGridLayout.width = (int) baseItemWidth;

            itemView.setLayoutParams(paramsGridLayout);
        }
    }

    class ViewHolderWall extends RecyclerView.ViewHolder {


        ViewHolderWall(LayoutInflater inflater, ViewGroup parent) {
            super(new View(context));

        }

        void bind(Location location, int x, int y) {

            itemView.setBackgroundColor(context.getResources().getColor(R.color.colorClusterMapWall));

            GridLayoutManager.LayoutParams paramsGridLayout = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            if (paramsGridLayout == null)
                paramsGridLayout = new GridLayoutManager.LayoutParams(0, 0);

            int paddingLeft = itemPadding2dp;
            int paddingTop = itemPadding2dp;
            int paddingRight = itemPadding2dp;
            int paddingBottom = itemPadding2dp;

            Location left = getPosition(x - 1, y);
            if (left != null && left.kind == Location.Kind.WALL)
                paddingLeft = 0;
            Location top = getPosition(x, y - 1);
            if (top != null && top.kind == Location.Kind.WALL)
                paddingTop = 0;
            Location right = getPosition(x + 1, y);
            if (right != null && right.kind == Location.Kind.WALL)
                paddingRight = 0;
            Location end = getPosition(x, y + 1);
            if (end != null && end.kind == Location.Kind.WALL)
                paddingBottom = 0;

            paramsGridLayout.height = (int) (baseItemHeight - paddingTop - paddingBottom);
            paramsGridLayout.width = (int) (baseItemWidth - paddingLeft - paddingRight);
            paramsGridLayout.setMargins(paddingLeft, paddingTop, paddingRight, paddingBottom);

            itemView.setLayoutParams(paramsGridLayout);

        }

        Location getPosition(int x, int y) {
            if (x < 0 ||
                    y < 0 ||
                    cluster.map.length <= x ||
                    cluster.map[x] == null ||
                    cluster.map[x].length <= y)
                return null;
            return cluster.map[x][y];
        }
    }
}
