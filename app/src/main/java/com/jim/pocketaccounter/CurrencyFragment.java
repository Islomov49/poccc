package com.jim.pocketaccounter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jim.pocketaccounter.finance.CurrencyAdapter;
import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PockerTag;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.ScrollDirectionListener;

@SuppressLint("InflateParams")
public class CurrencyFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private FloatingActionButton fabCurrencyAdd;
	private ListView lvCurrency;
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
	private boolean[] selected;
	private ImageView ivToolbar, ivToolbarExcel;
	private Spinner spToolbar;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.currency_fragment, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);

		fabCurrencyAdd = (FloatingActionButton) rootView.findViewById(R.id.fabCurrencyAdd);
		fabCurrencyAdd.setOnClickListener(this);
		lvCurrency = (ListView) rootView.findViewById(R.id.lvCurrency);
		lvCurrency.setOnItemClickListener(this);
		fabCurrencyAdd.attachToListView(lvCurrency, new ScrollDirectionListener() {
			@Override
			public void onScrollUp() {
				if (mode == PocketAccounterGeneral.EDIT_MODE) return;
				if (fabCurrencyAdd.getVisibility() == View.GONE) return;
				Animation down = AnimationUtils.loadAnimation(getContext(), R.anim.fab_down);
				synchronized (down) {
					down.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							fabCurrencyAdd.setClickable(false);
							fabCurrencyAdd.setVisibility(View.GONE);
						}
						@Override
						public void onAnimationEnd(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					fabCurrencyAdd.startAnimation(down);
				}
			}
			@Override
			public void onScrollDown() {
				if (mode == PocketAccounterGeneral.EDIT_MODE) return;
				if (fabCurrencyAdd.getVisibility() == View.VISIBLE) return;
				Animation up = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_up);
				synchronized (up) {
					up.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							fabCurrencyAdd.setVisibility(View.VISIBLE);
							fabCurrencyAdd.setClickable(true);
						}
						@Override
						public void onAnimationEnd(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					fabCurrencyAdd.startAnimation(up);
				}
			}
		});
		((PocketAccounter)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
		PocketAccounter.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case android.R.id.home:
						PocketAccounter.drawer.openLeftSide();
						break;
				}
				return false;
			}
		});
		PocketAccounter.toolbar.setTitle(getResources().getString(R.string.currencies));
		PocketAccounter.toolbar.setSubtitle(getResources().getString(R.string.main_currency)+" "+ PocketAccounter.financeManager.getMainCurrency().getAbbr());
		((PocketAccounter)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PocketAccounter.drawer.openLeftSide();
			}
		});
		ivToolbar = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
		ivToolbar.setVisibility(View.VISIBLE);
		ivToolbar.setImageResource(R.drawable.pencil);
		ivToolbar.setOnClickListener(this);
		ivToolbarExcel = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel);
		ivToolbarExcel.setVisibility(View.GONE);
		spToolbar = (Spinner) PocketAccounter.toolbar.findViewById(R.id.spToolbar);
		spToolbar.setVisibility(View.GONE);
		refreshList();
		return rootView;
	}
	private void setEditMode() {
		mode = PocketAccounterGeneral.EDIT_MODE;
		selected = new boolean[PocketAccounter.financeManager.getCurrencies().size()];
		for (int i=0; i<selected.length; i++)
			selected[i] = false;
		ivToolbar.setImageDrawable(null);
		ivToolbar.setImageResource(R.drawable.ic_delete_black);
		Animation fabDown = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_down);
		fabDown.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				fabCurrencyAdd.setVisibility(View.GONE);
			}
		});
		fabCurrencyAdd.startAnimation(fabDown);
		fabCurrencyAdd.setClickable(false);
		refreshList();
	}
	private void setCurrencyListMode() {
		mode = PocketAccounterGeneral.NORMAL_MODE;
		ivToolbar.setImageDrawable(null);
		ivToolbar.setImageResource(R.drawable.pencil);
		Animation fabUp = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_up);
		fabUp.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {	fabCurrencyAdd.setVisibility(View.VISIBLE);}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});
		fabCurrencyAdd.startAnimation(fabUp);
		fabCurrencyAdd.setClickable(true);
		refreshList();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fabCurrencyAdd:
				((PocketAccounter)getActivity()).replaceFragment(new CurrencyChooseFragment());
				break;
			case R.id.ivToolbarMostRight:
				if (PocketAccounter.financeManager.getCurrencies().size() == 1) {
					Toast.makeText(getActivity(), getResources().getString(R.string.currency_empty_warning), Toast.LENGTH_SHORT).show();
					return;
				}
				if (mode == PocketAccounterGeneral.NORMAL_MODE) {
					setEditMode();
				}
				else {
					boolean selection = false;
					for (int i=0; i<selected.length; i++) {
						if (selected[i]) {
							selection = true;
							break;
						}
					}
					if (!selection) {
						setCurrencyListMode();
						return;
					}
					final Dialog dialog=new Dialog(getActivity());
					View dialogView = getActivity().getLayoutInflater().inflate(R.layout.warning_dialog, null);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(dialogView);
					TextView tvWarningText = (TextView) dialogView.findViewById(R.id.tvWarningText);
					tvWarningText.setText(getResources().getString(R.string.currency_delete_warning));
					Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
					Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
					btnYes.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							boolean foundNoneSelected = false;
							for (int i=0; i<selected.length; i++) {
								if (!selected[i]) {
									foundNoneSelected = true;
									break;
								}
							}
							if (!foundNoneSelected) {
								for (int i=0; i<PocketAccounter.financeManager.getRecords().size(); i++) {
									if (PocketAccounter.financeManager.getRecords().get(i).getCurrency().getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
										PocketAccounter.financeManager.getRecords().remove(i);
										i--;
									}
								}
								for (int i=0; i<PocketAccounter.financeManager.getDebtBorrows().size(); i++) {
									if (PocketAccounter.financeManager.getDebtBorrows().get(i).getCurrency().getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
										PocketAccounter.financeManager.getDebtBorrows().remove(i);
										i--;
									}
								}
								for (int i=0; i<PocketAccounter.financeManager.getCredits().size(); i++) {
									if (PocketAccounter.financeManager.getCredits().get(i).getValyute_currency().getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
										PocketAccounter.financeManager.getCredits().remove(i);
										i--;
									}
								}
								for (int i=0; i<PocketAccounter.financeManager.getSmsObjects().size(); i++) {
									if (PocketAccounter.financeManager.getSmsObjects().get(i).getCurrency().getId().matches(PocketAccounter.financeManager.getMainCurrency().getId())) {
										PocketAccounter.financeManager.getSmsObjects().remove(i);
										i--;
									}
								}
								for (int i = 0; i< PocketAccounter.financeManager.getCurrencies().size(); i++) {
									if (!PocketAccounter.financeManager.getCurrencies().get(i).getMain()) {
										PocketAccounter.financeManager.getCurrencies().remove(i);
										i--;
									}
								}
							} else {
								for (int i=0; i<selected.length; i++) {
									if (!selected[i]) continue;
									for (int j=0; j<PocketAccounter.financeManager.getRecords().size(); j++) {
										if (PocketAccounter.financeManager.getRecords().get(j).getCurrency().getId().matches(PocketAccounter.financeManager.getCurrencies().get(i).getId())) {
											PocketAccounter.financeManager.getRecords().remove(j);
											j--;
											Log.d("sss", "entered");
										}
									}
									for (int j=0; j<PocketAccounter.financeManager.getDebtBorrows().size(); j++) {
										if (PocketAccounter.financeManager.getDebtBorrows().get(j).getCurrency().getId().matches(PocketAccounter.financeManager.getCurrencies().get(i).getId())) {
											PocketAccounter.financeManager.getDebtBorrows().remove(j);
											j--;
										}
									}
									for (int j=0; j<PocketAccounter.financeManager.getCredits().size(); j++) {
										if (PocketAccounter.financeManager.getCredits().get(j).getValyute_currency().getId().matches(PocketAccounter.financeManager.getCurrencies().get(i).getId())) {
											PocketAccounter.financeManager.getCredits().remove(j);
											j--;
										}
									}
									for (int j=0; j<PocketAccounter.financeManager.getSmsObjects().size(); j++) {
										if (PocketAccounter.financeManager.getSmsObjects().get(j).getCurrency().getId().matches(PocketAccounter.financeManager.getCurrencies().get(i).getId())) {
											PocketAccounter.financeManager.getSmsObjects().remove(j);
											j--;
										}
									}
								}
								for (int i=0; i<selected.length; i++) {
									if (selected[i]) {
										if (PocketAccounter.financeManager.getCurrencies().get(i).getMain()) {
											if (i==selected.length-1) {
												for (int j = 0; j< PocketAccounter.financeManager.getCurrencies().size(); j++) {
													if (PocketAccounter.financeManager.getCurrencies().get(j) != null) {
														PocketAccounter.financeManager.getCurrencies().get(j).setMain(true);
														break;
													}
												}
											} else {
												PocketAccounter.financeManager.getCurrencies().get(i+1).setMain(true);
											}
										}
										PocketAccounter.financeManager.getCurrencies().set(i, null);
									}
								}

								for (int i = 0; i< PocketAccounter.financeManager.getCurrencies().size(); i++) {
									if (PocketAccounter.financeManager.getCurrencies().get(i) == null) {
										PocketAccounter.financeManager.getCurrencies().remove(i);
										i--;
									}
								}
							}
							PocketAccounter.financeManager.saveCurrencies();
							PocketAccounter.financeManager.saveRecords();
							PocketAccounter.financeManager.saveSmsObjects();
							PocketAccounter.financeManager.setRecords(PocketAccounter.financeManager.loadRecords());
							setCurrencyListMode();
							PocketAccounter.toolbar.setSubtitle(getResources().getString(R.string.main_currency)+" "+ PocketAccounter.financeManager.getMainCurrency().getAbbr());
							dialog.dismiss();
						}
					});
					btnNo.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
				break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (view != null) {
			if (mode == PocketAccounterGeneral.EDIT_MODE) {
				CheckBox chbCurrencyEdit = (CheckBox) view.findViewById(R.id.chbCurrencyEdit);
				chbCurrencyEdit.setChecked(!chbCurrencyEdit.isChecked());
				selected[position] = chbCurrencyEdit.isChecked();
			} else {
				if (PocketAccounter.financeManager.getCurrencies().get(position).getMain()) {
					Toast.makeText(getActivity(), getResources().getString(R.string.main_currency_edit), Toast.LENGTH_SHORT).show();
					return;
				}
				((PocketAccounter) getActivity()).replaceFragment(new CurrencyEditFragment(PocketAccounter.financeManager.getCurrencies().get(position)));
			}
		}
	};
	private void refreshList() {
		CurrencyAdapter adapter = new CurrencyAdapter(getActivity(), PocketAccounter.financeManager.getCurrencies(), selected, mode);
		lvCurrency.setAdapter(adapter);
	}
	@Override
	public void onStop() {
		super.onStop();
		PocketAccounter.financeManager.saveCurrencies();
	}
}