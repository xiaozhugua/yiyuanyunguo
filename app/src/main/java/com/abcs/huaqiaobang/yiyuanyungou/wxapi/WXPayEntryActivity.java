package com.abcs.huaqiaobang.yiyuanyungou.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyUpdateUI;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, "wxe134ccc9e61300a0");
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onResp(com.tencent.mm.sdk.
	 * modelbase.BaseResp)
	 */
	@Override
	public void onResp(BaseResp resp) {
		Log.i("zjz", "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// if (resp.errCode == 0) {
			// builder.setMessage("支付成功");
			// } else if (resp.errCode == -1) {
			// builder.setMessage("支付异常,请重新支付或联系客服");
			// } else if (resp.errCode == -2) {
			// builder.setMessage("已取消支付");
			// }
			// // builder.setMessage(getString(R.string.pay_result_callback_msg,
			// // String.valueOf(resp.errCode)));
			// builder.show();
			if (resp.errCode == 0) {
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				MyUpdateUI.sendUpdateCollection(this, MyUpdateUI.ORDER);
				MyUpdateUI.sendUpdateCollection(this, MyUpdateUI.YYGBUY);
				Log.i("zjz", "支付成功！");
//				if (MyApplication.getInstance().getNowActivity() instanceof ReSearchActivity) {
//					((ReSearchActivity) MyApplication.getInstance().getNowActivity()).paysuccess("支付成功");
//				} else if (MyApplication.getInstance().getNowActivity() instanceof LaunchReSearchActivity) {
//					((LaunchReSearchActivity) MyApplication.getInstance().getNowActivity()).paysuccess("支付成功");
//				} else if (MyApplication.getInstance().getNowActivity() instanceof MainActivity) {
//					((MainActivity) MyApplication.getInstance().getNowActivity()).paysuccess("支付成功");
//				}
			} else if (resp.errCode == -1) {
				Toast.makeText(this, "支付异常,请重新支付或联系客服", Toast.LENGTH_SHORT).show();
			} else if (resp.errCode == -2) {
				Toast.makeText(this, "已取消支付", Toast.LENGTH_SHORT).show();
			}
		}
		finish();
	}
}