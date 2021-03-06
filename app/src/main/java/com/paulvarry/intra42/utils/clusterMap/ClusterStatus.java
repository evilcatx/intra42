package com.paulvarry.intra42.utils.clusterMap;

import android.util.SparseArray;

import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClusterStatus {

    public List<Cluster> clusters;

    /**
     * key : Location name
     * <p>
     * Value : User on this location
     */
    public HashMap<String, UsersLTE> locations;
    public SparseArray<FriendsSmall> friends;

    public ClusterMapActivity.LayerStatus layerStatus = ClusterMapActivity.LayerStatus.FRIENDS;
    public String layerUserLogin = "";

    public SparseArray<ProjectsUsers> projectsUsers;
    public String layerProjectSlug = "";
    public ProjectsUsers.Status layerProjectStatus = ProjectsUsers.Status.IN_PROGRESS;
    public String layerLocationPost = "";

    public ClusterStatus() {

    }

    public void initClusterList(List<Cluster> cluster) {
        if (cluster == null) return;
        this.clusters = cluster;
        Collections.sort(cluster);
    }

    public void computeFreePosts() {
        if (clusters == null)
            return;
        for (Cluster cluster : clusters) {
            cluster.computeFreePosts(locations);
        }
    }

    public void computeHighlightPosts() {
        if (clusters == null)
            return;
        for (Cluster cluster : clusters) {
            cluster.computeHighlightPosts(this);
        }
    }

    public UsersLTE getUserInLocation(Location locationItem) {
        if (locationItem.kind == Location.Kind.USER &&
                locationItem.host != null &&
                !locationItem.host.isEmpty() &&
                locations != null)
            return locations.get(locationItem.host);
        return null;
    }

    public void computeHighlightAndFreePosts() {
        if (clusters == null)
            return;
        for (Cluster cluster : clusters) {
            cluster.computeHighlightAndFreePosts(this, locations);
        }
    }

    public Cluster getCluster(String prefix) {
        if (clusters == null)
            return null;
        for (Cluster c : clusters) {
            if (c.hostPrefix.contentEquals(prefix))
                return c;
        }
        return null;
    }
}
