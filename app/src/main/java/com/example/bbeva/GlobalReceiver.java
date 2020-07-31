package com.example.bbeva;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import ru.evotor.framework.core.action.event.cash_drawer.CashDrawerOpenEvent;
import ru.evotor.framework.core.action.event.cash_operations.CashInEvent;
import ru.evotor.framework.core.action.event.cash_operations.CashOutEvent;
import ru.evotor.framework.core.action.event.inventory.ProductCardOpenedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionAddedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionEditedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionRemovedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClearedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

/**
 * Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека
 * Смарт терминал не ждёт ответ от приложения на широковещательные сообщения.
 */
public class GlobalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.e(getClass().getSimpleName(), action);

        if (action != null) {
            switch (action) {
                case "evotor.intent.action.receipt.sell.RECEIPT_CLOSED":

                    Toast.makeText(context, "HEHE", Toast.LENGTH_SHORT).show();
                    Receipt receipt = ReceiptApi.getReceipt(context, ReceiptOpenedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, receipt.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}