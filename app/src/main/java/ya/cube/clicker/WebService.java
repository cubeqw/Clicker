package ya.cube.clicker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WebService {
    @GET("--")
    Call<String> url(@Query("url") String url);
}
