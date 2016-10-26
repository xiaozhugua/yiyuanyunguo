package com.abcs.huaqiaobang.yiyuanyungou.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.abcs.huaqiaobang.yiyuanyungou.MyApplication;
import com.abcs.huaqiaobang.yiyuanyungou.R;
import com.abcs.huaqiaobang.yiyuanyungou.activity.EditAddressActivity;
import com.abcs.huaqiaobang.yiyuanyungou.adapter.viewHolder.AddressRecyclerViewHolder;
import com.abcs.huaqiaobang.yiyuanyungou.beans.Address;
import com.abcs.huaqiaobang.yiyuanyungou.broadcast.MyUpdateUI;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.ProgressDlgUtil;
import com.abcs.huaqiaobang.yiyuanyungou.dialog.PromptDialog;
import com.abcs.huaqiaobang.yiyuanyungou.util.Complete;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRequest;
import com.abcs.huaqiaobang.yiyuanyungou.util.HttpRevMsg;
import com.abcs.huaqiaobang.yiyuanyungou.util.TLUrl;
import com.abcs.huaqiaobang.yiyuanyungou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zjz on 2016/1/12.
 */
public class AddressRecyclerAdapter extends RecyclerView.Adapter<AddressRecyclerViewHolder> {


    ArrayList<Address> address;
    Context context;
    public static HashMap<Integer, Boolean> isSelected;
    public static HashMap<Integer, String> isMoren;
    boolean isBuy;
    boolean isYYG;
    Activity activity;
    private List<Integer>checkPositon;
    public ArrayList<Address> getDatas() {
        return address;
    }

    public Handler handler = new Handler();

    public AddressRecyclerAdapter(Activity activity, boolean isBuy, boolean isYYG) {
        this.activity = activity;
        this.isBuy = isBuy;
        this.isYYG=isYYG;
        this.address = new ArrayList<>();
        this.checkPositon=new ArrayList<>();
    }

    @Override
    public AddressRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hwg_item_address, parent, false);
        AddressRecyclerViewHolder hwgFragmentViewHolder = new AddressRecyclerViewHolder(view);
        this.context = parent.getContext();
        return hwgFragmentViewHolder;
    }

    @Override
    public void onBindViewHolder(final AddressRecyclerViewHolder mHolder, final int position) {
        mHolder.t_name.setText(address.get(position).getTrue_name());
        char[] chars = address.get(position).getPhone().toCharArray();
        String str = address.get(position).getPhone();
        if (str.length() > 7) {
            String first = str.substring(0, 3);
            String last = str.substring(str.length() - 4, str.length());
            mHolder.t_phone.setText(first + "****" + last);
        } else {
            mHolder.t_phone.setText(str);
        }
//        mHolder.t_phone.setText(chars[0]+chars[1]+chars[2]+"***"+chars[chars.length-4]+chars[chars.length-3]+chars[chars.length-2]+chars[chars.length-1]);
//        mHolder.t_phone.setText(address.get(position).getPhone());


        mHolder.t_address.setText(address.get(position).getArea_info() + " " + address.get(position).getDetail_address());
        mHolder.t_id_card.setText("身份证信息：" + address.get(position).getId_card());
//        mHolder.img_default.setVisibility(address.get(position).getIs_default().equals("1")?View.VISIBLE:View.GONE);
        mHolder.radio_check.setTag(new Integer(position));
        if (address.get(position).getIs_default().equals("1")){
            mHolder.t_default.setTextColor(activity.getResources().getColor(R.color.tljr_statusbarcolor));
            mHolder.t_default.setText("默认地址");
        }else {
            mHolder.t_default.setTextColor(activity.getResources().getColor(R.color.hwg_text2));
            mHolder.t_default.setText("设为默认");
        }
        mHolder.radio_check.setChecked(address.get(position).getIs_default().equals("1"));
//        mHolder.radio_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    if (!checkPositon.contains(mHolder.radio_check.getTag()))
//                        checkPositon.add(new Integer(position));
//                } else {
//                    if (checkPositon.contains(mHolder.radio_check.getTag()))
//                        checkPositon.remove(new Integer(position));
//                }
//            }
//        });
//        if(checkPositon!=null&&checkPositon.contains(new Integer(position))){
//            if (address.get(position).getIs_default().equals("1")){
//                mHolder.t_default.setTextColor(activity.getResources().getColor(R.color.tljr_statusbarcolor));
//                mHolder.t_default.setText("默认地址");
//            }
//        }
        mHolder.radio_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRequest.sendPost(TLUrl.URL_hwg_address_addedit + "&id=" + address.get(position).getAddress_id(), "key=" + MyApplication.getInstance().getMykey() + "&true_name="
                        + address.get(position).getTrue_name() + "&mob_phone=" + address.get(position).getPhone() + "&city_id=" + address.get(position).getCity_id() + "&area_id=" +
                        address.get(position).getArea_id() + "&address=" + address.get(position).getDetail_address() + "&area_info=" + address.get(position).getArea_info() +
                        "&is_default=" + "1" + "&id_card=" + "&address_id=" + address.get(position).getAddress_id(), new HttpRevMsg() {
                    @Override
                    public void revMsg(final String msg) {
                        // TODO Auto-generated method stub
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject json = new JSONObject(msg);
                                    if (json != null && json.has("code")) {
                                        int code = json.getInt("code");
                                        Log.i("zjz", "修改默认成功");
                                        Log.i("zjz", "address_edit" + msg);
                                        if (code == 200) {
                                            if (json.optString("datas").contains("成功")) {
//                                                notifyDataSetChanged();
                                                MyUpdateUI.sendUpdateCollection(activity, MyUpdateUI.ADDRESS);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    Log.i("zjz", "登录失败！");
                                    Log.i("zjz", msg);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }


                });
            }
        });
//        mHolder.radio_check.setChecked(isSelected.get(position));
//        if (isMoren.get(position).equals("默认地址")) {
//            mHolder.t_default.setTextColor(Color.parseColor("#eb5041"));
//        } else {
//            mHolder.t_default.setTextColor(Color.parseColor("#555555"));
//        }
//        mHolder.t_default.setText(isMoren.get(position));
//        mHolder.radio_check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isSelected.put(position, true);
//                isMoren.put(position, "默认地址");
//                SharedPreferences.Editor editor = Util.preference.edit();
//                editor.putBoolean("isDefault", true);
//                editor.putString("address_id", address.get(position).getAddress_id());
//                editor.putString("area_id", address.get(position).getArea_id());
//                editor.putString("city_id", address.get(position).getCity_id());
//                editor.putString("address", mHolder.t_address.getText().toString());
//                editor.putString("name", mHolder.t_name.getText().toString());
//                editor.putString("phone", mHolder.t_phone.getText().toString());
//                editor.commit();
////                AddressActivity.isCheck = address.getAddress_id();
////                Log.i("zjz", "addr_id=" + address.getAddress_id());
////                if (buttonView.isChecked()) {
//                for (int i = 0; i < addressList.size(); i++) {
//                    //把其他的checkbox设置为false
//                    if (i != position) {
//                        isSelected.put(i, false);
//                        isMoren.put(i, "设置默认");
//                    }
//                }
////                }
//                //通知适配器更改
//                notifyDataSetChanged();
//            }
//        });


//        mHolder.linear_root.setEnabled(isBuy);
        mHolder.linear_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBuy) {
                    Intent intent = new Intent();
                    intent.putExtra("address", mHolder.t_address.getText());
                    intent.putExtra("name", mHolder.t_name.getText());
                    intent.putExtra("phone", mHolder.t_phone.getText());
                    intent.putExtra("idCard", address.get(position).getId_card());
                    intent.putExtra("address_id", address.get(position).getAddress_id());
                    intent.putExtra("area_id", address.get(position).getArea_id());
                    intent.putExtra("city_id", address.get(position).getCity_id());
                    activity.setResult(1, intent);
                    activity.finish();
                } else if(isYYG){
                    Intent intent = new Intent();
                    intent.putExtra("address_info", address.get(position).getArea_info());
                    intent.putExtra("address_detail",address.get(position).getDetail_address());
                    intent.putExtra("name", mHolder.t_name.getText());
                    intent.putExtra("phone", address.get(position).getPhone());
                    intent.putExtra("address_id", address.get(position).getAddress_id());
                    activity.setResult(1, intent);
                    activity.finish();

                } else {
                    Intent intent = new Intent(activity, EditAddressActivity.class);
                    intent.putExtra("title", "修改收货地址");
                    intent.putExtra("isEdit", true);
                    intent.putExtra("address_id", address.get(position).getAddress_id());
                    activity.startActivity(intent);
                }

            }
        });
        mHolder.linear_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditAddressActivity.class);
                intent.putExtra("title", "修改收货地址");
                intent.putExtra("isEdit", true);
                intent.putExtra("address_id", address.get(position).getAddress_id());
                activity.startActivity(intent);
            }
        });
        mHolder.linear_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PromptDialog(activity, "确定删除该地址？", new Complete() {
                    @Override
                    public void complete() {
                        ProgressDlgUtil.showProgressDlg("", activity);
                        HttpRequest.sendPost(TLUrl.URL_hwg_address_del, "&address_id=" + address.get(position).getAddress_id() + "&key=" + MyApplication.getInstance().getMykey(), new HttpRevMsg() {
                            @Override
                            public void revMsg(final String msg) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject object = new JSONObject(msg);
                                            if (object.getInt("code") == 200) {
                                                Log.i("zjz", "msg=" + msg);
                                                ProgressDlgUtil.stopProgressDlg();
                                                if (Util.preference != null && address.get(position).getAddress_id().equals(Util.preference.getString("address_id", ""))) {
                                                    Util.preference.edit().putBoolean("isDefault", false).commit();
                                                    Util.preference.edit().putString("address_id", "").commit();
                                                    Util.preference.edit().putString("area_id", "").commit();
                                                    Util.preference.edit().putString("city_id", "").commit();
                                                    Util.preference.edit().putString("address", "").commit();
                                                    Util.preference.edit().putString("name", "").commit();
                                                    Util.preference.edit().putString("phone", "").commit();
                                                }
                                                MyUpdateUI.sendUpdateCollection(activity, MyUpdateUI.ADDRESS);
                                            } else {
                                                ProgressDlgUtil.stopProgressDlg();
                                                Log.i("zjz", "goodsDetail:解析失败");
                                            }
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            Log.i("zjz", e.toString());
                                            Log.i("zjz", msg);
                                            e.printStackTrace();
                                            ProgressDlgUtil.stopProgressDlg();
                                        }
                                    }
                                });

                            }
                        });
                    }
                }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return address.size();
    }
}
