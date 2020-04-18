package com.squadro.touricity.view.map.DirectionsAPI;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.PathVertex;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.map.PolylineDrawer;
import com.squadro.touricity.view.routeList.RouteCreateView;

import java.util.ArrayList;
import java.util.List;

public class PointListReturner implements IAsync, IAsync2{

    private List<LatLng> pointList = null;

    private RouteCreateView rcw = null;

    private int pathIndex;
    private Context context;
    private Path path = new Path(null,0,0,"",null, Path.PathType.DRIVING,null);

    public PointListReturner(String url, RouteCreateView route, int newPathIndex, Context context){
        FetchUrl fetchUrl = new FetchUrl(this);
        fetchUrl.execute(url);
        this.rcw = route;
        this.pathIndex = newPathIndex;
        this.context = context;
    }

    @Override
    public void onComplete(String data) {
        ParserTask parser = new ParserTask(this);
        parser.execute(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onComplete2(List<LatLng> data, int seconds) {

        this.path.setVertices(latlonListToPathVertexList(data));
        this.path.setDuration(seconds);
        ((Path)(rcw.getRoute().getAbstractEntryList().get(pathIndex))).setVertices(latlonListToPathVertexList(data));
        ((Path)(rcw.getRoute().getAbstractEntryList().get(pathIndex))).setDuration(seconds);

        PolylineDrawer pd = new PolylineDrawer(MapFragmentTab2.getMap(),"create",context);
        pd.drawRoute(rcw.getRoute());
    }

    public List<PathVertex> latlonListToPathVertexList(List<LatLng> latlonList){

        List<PathVertex> vertices = new ArrayList<>();

        for (LatLng ltln: latlonList) {
            PathVertex tmp = new PathVertex(ltln.latitude, ltln.longitude);
            vertices.add(tmp);
        }
        return vertices;
    }
}
