package com.example.bbeva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_NEW_PRODUCT = 10;
    public static final int REQUEST_CODE_FOR_EDIT_PRODUCT = 11;

    OkHttpClient client = new OkHttpClient();

    public String baseUrl= "http://192.168.1.105/";
    public String url = baseUrl + "api/evotor/longPulling";

    private static WarrantArrayAdapter warrantArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

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

    public void alert(){
        Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_LONG).show();
    }

    public static void catchResponse(String response, final MainActivity activity) throws JSONException {
        JSONObject obj = new JSONObject(response);
        JSONArray warrants = obj.getJSONArray("warrants");
        List<String[]> warrantItems = new ArrayList<String[]>();
        for (int i=0; i < warrants.length(); i++) {
            String[] warrant = new String[3];
            warrant[0] = "warrant";
            warrant[1] = "Кассовый ордер №" + warrants.getJSONObject(i).getString("id");
            warrant[2] = warrants.getJSONObject(i).getString("summ");
            warrantItems.add(warrant);
        }
        ListView warrantList = activity.findViewById(R.id.warrants);
        final TextView txt = activity.findViewById(R.id.txt);


        final TextView tv = activity.findViewById(R.id.tv);
        warrantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                //String selectedItem = (String) parent.getItemAtPosition(position);
                //Toast.makeText(activity, position, Toast.LENGTH_LONG).show();
                // Display the selected item text on TextView
                activity.alert();
                //tv.setText("Your favorite : " + selectedItem);
            }
        });
//


        warrantArrayAdapter = new WarrantArrayAdapter(activity, R.layout.warrant_list_item);
        warrantList.setAdapter(warrantArrayAdapter);



        for(String[] warrantData:warrantItems ) {
            String warrantImg = warrantData[0];
            String warrantName = warrantData[1];
            String warrant_sum = warrantData[2];
            int warrantImgResId = activity.getResources().getIdentifier(warrantImg, "drawable", "com.javapapers.android.listviewcustomlayout.app");
            Warrant warrant = new Warrant(warrantImgResId,warrantName,warrant_sum);
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


    public void call() {
//        Toast.makeText(this, "вызываем продажу", Toast.LENGTH_LONG).show();
//        startActivityForResult(NavigationApi.createIntentForNewProduct(
//                new NavigationApi.NewProductIntentBuilder().setBarcode("111")
//        ), 10);
        List<Position> list = new ArrayList<>();
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        null,
                        //Наименование
                        "12234",
                        //Наименование единицы измерения
                        "12",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(1000),
                        //Количество
                        new BigDecimal(2)
                ).build()
        );
        list.add(
                Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "1234",
                        "12",
                        0,
                        new BigDecimal(500),
                        new BigDecimal(1))
                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        .setPriceWithDiscountPosition(new BigDecimal(300)).build()
        );

        String randomUUID = UUID.randomUUID().toString();
        //List<HashMap> payments = new ArrayList<>();
        HashMap payments = new HashMap<Payment, BigDecimal>();
        payments.put(new Payment(
                randomUUID,
                new BigDecimal(9300 ),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "Internet", "124224"),
                        "имя пакета",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(9300 ));

        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(), PrintGroup.Type.CASH_RECEIPT, null, null, null, null, true);

        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(printGroup, list, payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);
        //Добавление скидки на чек
        BigDecimal receiptDiscount = new BigDecimal(1000);
        new PrintSellReceiptCommand(listDocs, null, "79524365062", "exam2ple@example.com", receiptDiscount, "ad", "awd").process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}

