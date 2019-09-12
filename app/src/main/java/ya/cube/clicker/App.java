package ya.cube.clicker;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class App extends Application {

    private static WebService webService;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://clck.ru/") //Базовая часть адреса
                .addConverterFactory(ScalarsConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        webService = retrofit.create(WebService.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static WebService getApi() {
        return webService;
    }
}