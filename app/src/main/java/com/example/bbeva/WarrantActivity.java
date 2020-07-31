package com.example.bbeva;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
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
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;

public class WarrantActivity extends AppCompatActivity {

    private Boolean payed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Warrant warrant = (Warrant)intent.getSerializableExtra("Warrant");

        Objects.requireNonNull(getSupportActionBar()).setTitle(warrant.getWarrantName());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setSubtitle("главная страница");

        //Toast.makeText(WarrantActivity.this, warrant.getWarrantName(), Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_warrant);
        //getActionBar().setTitle("awdawd");
        TextView created_at = findViewById(R.id.created_at);
        created_at.setText(warrant.getWarrantCreatedAt());

        TextView cashbox = findViewById(R.id.cashbox);
        cashbox.setText(warrant.getWarrantCashbox());

        TextView dds = findViewById(R.id.dds);
        dds.setText(warrant.getWarrantDds());

        TextView summ = findViewById(R.id.summ);
        summ.setText(warrant.getWarrantSum());

        TextView reason = findViewById(R.id.reason);
        reason.setText(warrant.getWarrantReason());

        TextView comment = findViewById(R.id.comment);
        comment.setText(warrant.getWarrantComment());

        final Button button = findViewById(R.id.pay);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay();
            }
        });
    }

    public void CheckPayed(){
        Receipt receipt = ReceiptApi.getReceipt(this, "awddd");
    }

    void pay(){
        call();
    }

    public void call() {
        //Toast.makeText(this, "вызываем продажу", Toast.LENGTH_LONG).show();
//        startActivityForResult(NavigationApi.createIntentForNewProduct(
//                new NavigationApi.NewProductIntentBuilder().setBarcode("111")
//        ), 10);
        openReceipt();
        //openReceiptAndEmail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openReceiptAndEmail() {
        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        null,
                        //Наименование
                        "1234",
                        //Наименование единицы измерения
                        "12",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(1000),
                        //Количество
                        BigDecimal.TEN
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
                        BigDecimal.ONE)
                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        .setPriceWithDiscountPosition(new BigDecimal(300)).build()
        );
        //Способ оплаты
        HashMap payments = new HashMap<Payment, BigDecimal>();
        Object put = payments.put(new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(9300),
                new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424"),
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424"),
                        "имя пакета",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(9300));
        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(), PrintGroup.Type.CASH_RECEIPT, null, null, null, null, true, null, null);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                list,
                payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);
        //Добавление скидки на чек
        BigDecimal receiptDiscount = new BigDecimal(1000);
        new PrintSellReceiptCommand(listDocs, null, "79011234567", "example@example.com", receiptDiscount, "", "").process(WarrantActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            //Toast.makeText(WarrantActivity.this, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            //Toast.makeText(WarrantActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    public void PayRecepeit(){
        //Получаем текущий открытый чек.
        Receipt receipt = ReceiptApi.getReceipt(WarrantActivity.this, Receipt.Type.SELL);
        if (receipt == null){
            Toast.makeText(WarrantActivity.this, "Чека нет", Toast.LENGTH_LONG).show();
            return;
        }
        //Получаем идентификатор чека.
        String uuid = receipt.getHeader().getUuid();
        if (uuid == null){
            Toast.makeText(WarrantActivity.this, "UIDA нет", Toast.LENGTH_LONG).show();
            return;
        }
        //Создаём список всех компонентов, способных выполнить оплату.
        final List<PaymentPerformer> paymentPerformers = PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(getPackageManager());
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(WarrantActivity.this);

        //Показываем пользователю диалоговое окно с возможностью выбрать исполнителя платежа, например, Наличными или Банковской картой.
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WarrantActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < paymentPerformers.size(); i++) {
            arrayAdapter.add(paymentPerformers.get(i).getPaymentSystem().getUserDescription());
        }
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            //По выбору пользователя выполняем оплату и печатаем чек.
            public void onClick(DialogInterface dialog, int which) {
                SellApi.moveCurrentReceiptDraftToPaymentStage(WarrantActivity.this, paymentPerformers.get(which), new ReceiptFormationCallback() {

                    @Override
                    public void onSuccess() {
                        //Toast.makeText(WarrantActivity.this, "Оплата прошла успешно", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(ReceiptFormationException e) {
                        //Toast.makeText(WarrantActivity.this, e.getCode() + " " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });
            }
        });
        builderSingle.show();
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
                                new BigDecimal(2),
                                //Количество
                                new BigDecimal(1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        )
//                                .setPriceWithDiscountPosition(new BigDecimal(0))
                                .setExtraKeys(set).build()
                )
        );

        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("ID", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, extra).process(WarrantActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        //startActivity(new Intent("evotor.intent.action.payment.SELL"));
                        PayRecepeit();
                    }

                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}