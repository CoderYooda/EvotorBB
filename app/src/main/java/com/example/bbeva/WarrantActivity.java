package com.example.bbeva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

public class WarrantActivity extends AppCompatActivity {

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

    void pay(){
        call();
    }

    public void call() {
        Toast.makeText(this, "вызываем продажу", Toast.LENGTH_LONG).show();
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
                        new PaymentSystem(PaymentType.CREDIT, "Internet", "124224"),
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
        new PrintSellReceiptCommand(listDocs, null, "79524365062", "exam2ple@example.com", receiptDiscount, "ad", "awd").process(WarrantActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(WarrantActivity.this, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            Toast.makeText(WarrantActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
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
}