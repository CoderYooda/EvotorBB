package com.example.bbeva;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

//import android.support.v7.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_NEW_PRODUCT = 10;
    public static final int REQUEST_CODE_FOR_EDIT_PRODUCT = 11;
    public static final int sec = 0;

    OkHttpClient client = new OkHttpClient();

    public String baseUrl= "http://192.168.1.105/";
    public String url = baseUrl + "api/evotor/getWarrantToPrint/7";

    private static WarrantArrayAdapter warrantArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Платёжная интеграция BBCRM");
        getSupportActionBar().setSubtitle("главная страница");

//        try {
//            start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        final Button button = findViewById(R.id.btnOpenReceipt);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void start() throws IOException {

        Toast.makeText(MainActivity.this, "СТАРТ", Toast.LENGTH_LONG).show();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Toast.makeText(MainActivity.this, "call.cancel", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainActivity.catchResponse(myResponse, MainActivity.this);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "catchResponse exe", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void alert(Warrant warrant){
        //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, WarrantActivity.class);
        intent.putExtra("Warrant", warrant);
        startActivity(intent);
    }

    public static void catchResponse(String response, final MainActivity activity) throws JSONException {
        JSONObject obj = new JSONObject(response);
        JSONObject warrant_obj = obj.getJSONObject("warrant");
        Warrant warrant = new Warrant(warrant_obj);
        activity.openReceipt(warrant);
    }

    public void openReceipt(final Warrant warrant) throws JSONException {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
//        Set<ExtraKey> set = new HashSet<>();
//        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();

        JSONArray items = warrant.getWarrantItems();

        for (int i = 0; i < items.length(); i++)
        {
//            Toast.makeText(MainActivity.this, (int) , Toast.LENGTH_LONG).show();

//            Integer price = Integer.parseInt(items.getJSONObject(i).getString("price"));
//            Integer count = Integer.parseInt(items.getJSONObject(i).getString("count"));

            positionAddList.add(
                new PositionAdd(
                    Position.Builder.newInstance(
                        UUID.randomUUID().toString(),//UUID позиции
                        null,//UUID товара
                        items.getJSONObject(i).getString("name"),//Наименование
                        "шт",//Наименование единицы измерения
                        0,//Точность единицы измерения
                        new BigDecimal(items.getJSONObject(i).getDouble("price")),//Цена без скидок Integer.parseInt(items.getJSONObject(i).getString("price"))  items.getJSONObject(i).getDouble("price")
                        new BigDecimal(items.getJSONObject(i).getDouble("count"))//Количество Integer.parseInt(items.getJSONObject(i).getString("count"))
                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                    )
                                .setPriceWithDiscountPosition(new BigDecimal(items.getJSONObject(i).getDouble("price") / 100 * (100 - warrant.getWarrantDiscount()) ))
//                                  .setExtraKeys(set)
                            .build()
                )
            );
        }



        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("warrant_id", warrant.getWarrantId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        //startActivity(new Intent("evotor.intent.action.payment.SELL"));
                        PayRecepeit(warrant);
                    }

                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void PayRecepeit(Warrant warrant){
        //Получаем текущий открытый чек.

        Receipt receipt = ReceiptApi.getReceipt(MainActivity.this, Receipt.Type.SELL);
        if (receipt == null){ Toast.makeText(MainActivity.this, "Чека нет", Toast.LENGTH_LONG).show();return; }
        String uuid = receipt.getHeader().getUuid();
        if (uuid == null){ Toast.makeText(MainActivity.this, "UIDA нет", Toast.LENGTH_LONG).show();return;}
        //Создаём список всех компонентов, способных выполнить оплату.


//        final List<PaymentPerformer> paymentPerformers = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(getPackageManager());
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);

        //Показываем пользователю диалоговое окно с возможностью выбрать исполнителя платежа, например, Наличными или Банковской картой.
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
//        for (int i = 0; i < paymentPerformers.size(); i++) {
//            arrayAdapter.add(paymentPerformers.get(i).getPaymentSystem().getUserDescription());
//        }

        PaymentPerformer payment = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(getPackageManager()).get(warrant.getWarrantPayment());

        SellApi.moveCurrentReceiptDraftToPaymentStage(MainActivity.this, payment, new ReceiptFormationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Оплата прошла успешно", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(ReceiptFormationException e) {
                Toast.makeText(MainActivity.this, e.getCode() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
//        builderSingle.show();
    }
}

