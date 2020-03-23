package com.squadro.touricity.view.map.DirectionsAPI;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;
import com.squadro.touricity.message.types.Path;
import com.squadro.touricity.message.types.Route;
import com.squadro.touricity.view.map.MapFragmentTab2;
import com.squadro.touricity.view.routeList.RouteCardView;
import com.squadro.touricity.view.routeList.RouteCreateView;

import java.util.List;

public class PointListReturner implements IAsync, IAsync2{

    private List<LatLng> pointList = null;

    private RouteCreateView rcw = null;

    private Path path = new Path(null,0,0,"",null, Path.PathType.DRIVING,null);

    public PointListReturner(String url, RouteCreateView route){
        FetchUrl fetchUrl = new FetchUrl(this);
        fetchUrl.execute(url);
        this.rcw = route;
    }

    @Override
    public void onComplete(String data) {

        //System.out.println(data);

        ParserTask parser = new ParserTask(this);
        parser.execute(data);

        //parser.getPointList();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onComplete2(List<LatLng> data) {

        this.path.setVertices(data);
        rcw.getRoute().getAbstractEntryList().add(new Path(null,0, 0, "", null, Path.PathType.DRIVING, data));
    }
}
