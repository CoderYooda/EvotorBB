package com.example.bbeva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import java.io.IOException;

//import android.support.v7.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_NEW_PRODUCT = 10;
    public static final int REQUEST_CODE_FOR_EDIT_PRODUCT = 11;

    OkHttpClient client = new OkHttpClient();

    public String baseUrl= "http://192.168.0.10/";
    public String url = baseUrl + "api/evotor/longPulling";

    private static WarrantArrayAdapter warrantArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Платёжная интеграция BBCRM");
        getSupportActionBar().setSubtitle("главная страница");
        final Button button = findViewById(R.id.btnOpenReceipt);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void run() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
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
        JSONArray warrants = obj.getJSONArray("warrants");
        List<String[]> warrantItems = new ArrayList<String[]>();
        for (int i=0; i < warrants.length(); i++) {
            String[] warrant = new String[15];
            warrant[0] = "ic_up";
            warrant[1] = warrants.getJSONObject(i).getString("name");
            warrant[2] = warrants.getJSONObject(i).getString("summ");
            warrant[3] = warrants.getJSONObject(i).getString("isIncoming");
            warrant[4] = warrants.getJSONObject(i).getString("date");
            warrant[5] = warrants.getJSONObject(i).getString("id");
            warrant[6] = warrants.getJSONObject(i).getString("created_at");
            warrant[7] = warrants.getJSONObject(i).getString("partner");
            warrant[8] = warrants.getJSONObject(i).getString("partner_id");
            warrant[9] = warrants.getJSONObject(i).getString("cashbox");
            warrant[10] = warrants.getJSONObject(i).getString("cashbox_id");
            warrant[11] = warrants.getJSONObject(i).getString("dds");
            warrant[12] = warrants.getJSONObject(i).getString("dds_id");
            warrant[13] = warrants.getJSONObject(i).getString("comment");
            warrant[14] = warrants.getJSONObject(i).getString("reason");
            warrantItems.add(warrant);
        }
        ListView warrantList = activity.findViewById(R.id.warrants);
        final TextView txt = activity.findViewById(R.id.txt);


        final TextView tv = activity.findViewById(R.id.tv);
        warrantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Warrant warrant = (Warrant) parent.getItemAtPosition(position);
                activity.alert(warrant);
            }
        });

        warrantArrayAdapter = new WarrantArrayAdapter(activity, R.layout.warrant_list_item);
        warrantList.setAdapter(warrantArrayAdapter);

        for(String[] warrantData:warrantItems ) {
            String warrantImg = warrantData[0];
            String warrantName = warrantData[1];
            String warrant_sum = warrantData[2];
            String isIncoming = warrantData[3];
            String date = warrantData[4];
            Integer id = Integer.parseInt(warrantData[5]);
            String created_at = warrantData[6];
            String partner = warrantData[7];
            Integer partner_id = Integer.parseInt(warrantData[8]);
            String cashbox = warrantData[9];
            Integer cashbox_id = Integer.parseInt(warrantData[10]);
            String dds = warrantData[11];
            Integer dds_id = Integer.parseInt(warrantData[12]);
            String comment = warrantData[13];
            String reason = warrantData[14];
            Warrant warrant = new Warrant(warrantImg,warrantName,warrant_sum,isIncoming, date, id, created_at, partner, partner_id, cashbox, cashbox_id, dds, dds_id, comment, reason);
            warrantArrayAdapter.add(warrant);
        }
    }

    public void openReceipt() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                null,
                                //Наименование
                                "Зубочистки",
                                //Наименование единицы измерения
                                "кг",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(200),
                                //Количество
                                new BigDecimal(1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        ).setPriceWithDiscountPosition(new BigDecimal(100))
                                .setExtraKeys(set).build()
                )
        );

        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME RECEIPT OPEN");
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
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}

