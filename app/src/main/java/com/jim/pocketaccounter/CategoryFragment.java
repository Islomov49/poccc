package com.jim.pocketaccounter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jim.pocketaccounter.finance.CategoryAdapter;
import com.jim.pocketaccounter.finance.RootCategory;
import com.jim.pocketaccounter.helper.FloatingActionButton;
import com.jim.pocketaccounter.helper.PocketAccounterGeneral;
import com.jim.pocketaccounter.helper.ScrollDirectionListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment implements OnClickListener, OnItemClickListener, OnCheckedChangeListener {
	private FloatingActionButton fabCategoryAdd;
	private ListView lvCategories;
	private CheckBox chbCatIncomes, chbCatExpanses;
	private ImageView ivToolbarMostRight;
	private int mode = PocketAccounterGeneral.NORMAL_MODE;
	private boolean[] selected;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.category_layout, container, false);
		rootView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(PocketAccounter.keyboardVisible){
					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);}
			}
		},100);

		PocketAccounter.toolbar.setTitle(getResources().getString(R.string.category));
		PocketAccounter.toolbar.setSubtitle("");
		((ImageView)PocketAccounter.toolbar.findViewById(R.id.ivToolbarExcel)).setVisibility(View.GONE);
		((PocketAccounter)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((PocketAccounter)getContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
		PocketAccounter.toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PocketAccounter.drawer.openLeftSide();
			}
		});
		ivToolbarMostRight = (ImageView) PocketAccounter.toolbar.findViewById(R.id.ivToolbarMostRight);
		ivToolbarMostRight.setImageResource(R.drawable.pencil);
		ivToolbarMostRight.setOnClickListener(this);
		fabCategoryAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAccountAdd);
		fabCategoryAdd.setOnClickListener(this);
		lvCategories = (ListView) rootView.findViewById(R.id.lvAccounts);
		lvCategories.setOnItemClickListener(this);
		PocketAccounter.toolbar.findViewById(R.id.spToolbar).setVisibility(View.GONE);
		fabCategoryAdd.attachToListView(lvCategories, new ScrollDirectionListener() {
			@Override
			public void onScrollUp() {
				if (mode == PocketAccounterGeneral.EDIT_MODE) return;
				if (fabCategoryAdd.getVisibility() == View.GONE) return;
				Animation down = AnimationUtils.loadAnimation(getContext(), R.anim.fab_down);
				synchronized (down) {
					down.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							fabCategoryAdd.setClickable(false);
							fabCategoryAdd.setVisibility(View.GONE);
						}
						@Override
						public void onAnimationEnd(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					fabCategoryAdd.startAnimation(down);
				}
			}
			@Override
			public void onScrollDown() {
				if (mode == PocketAccounterGeneral.EDIT_MODE) return;
				if (fabCategoryAdd.getVisibility() == View.VISIBLE) return;
				Animation up = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_up);
				synchronized (up) {
					up.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							fabCategoryAdd.setVisibility(View.VISIBLE);
							fabCategoryAdd.setClickable(true);
						}
						@Override
						public void onAnimationEnd(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
					fabCategoryAdd.startAnimation(up);
				}
			}
		});
		chbCatIncomes = (CheckBox) rootView.findViewById(R.id.chbCatIncomes);
		chbCatIncomes.setOnCheckedChangeListener(this);
		chbCatExpanses = (CheckBox) rootView.findViewById(R.id.chbCatExpanses);
		chbCatExpanses.setOnCheckedChangeListener(this);
		setMode(mode);
		refreshList(mode);
		return rootView;
	}
	private void refreshList(int mode) {
		ArrayList<RootCategory> categories = new ArrayList<RootCategory>();
		for (int i = 0; i< PocketAccounter.financeManager.getCategories().size(); i++) {
			if (chbCatIncomes.isChecked()) {
				if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
					categories.add(PocketAccounter.financeManager.getCategories().get(i));
			}
			if(chbCatExpanses.isChecked()) {
				if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
					categories.add(PocketAccounter.financeManager.getCategories().get(i));
			}
		}
		CategoryAdapter adapter = new CategoryAdapter(getActivity(), categories, selected, mode);
		lvCategories.setAdapter(adapter);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fabAccountAdd:
				((PocketAccounter)getActivity()).replaceFragment(new RootCategoryEditFragment(null, PocketAccounterGeneral.NO_MODE, 0, null));
				break;
			case R.id.ivToolbarMostRight:
				if (mode == PocketAccounterGeneral.NORMAL_MODE) {
					mode = PocketAccounterGeneral.EDIT_MODE;
					setMode(mode);
				}
				else {
					mode = PocketAccounterGeneral.NORMAL_MODE;
					boolean isAnySelection = false;
					for (int i=0; i<selected.length; i++) {
						if (selected[i]) {
							isAnySelection = true;
							break;
						}
					}
					if (isAnySelection) {
						final Dialog dialog=new Dialog(getActivity());
						View dialogView = getActivity().getLayoutInflater().inflate(R.layout.warning_dialog, null);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(dialogView);
						TextView tv = (TextView) dialogView.findViewById(R.id.tvWarningText);
						tv.setText(R.string.category_delete_warning);
						Button btnYes = (Button) dialogView.findViewById(R.id.btnWarningYes);
						btnYes.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteCategories();
								setMode(mode);
								dialog.dismiss();
							}
						});
						Button btnNo = (Button) dialogView.findViewById(R.id.btnWarningNo);
						btnNo.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
					else
						setMode(mode);
				}

				break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			if (chbCatExpanses.isChecked() && chbCatIncomes.isChecked())
				((PocketAccounter) getActivity()).replaceFragment(new RootCategoryEditFragment(PocketAccounter.financeManager.getCategories().get(position), PocketAccounterGeneral.NO_MODE, 0, null));
			else if (chbCatExpanses.isChecked() && !chbCatIncomes.isChecked()) {
				ArrayList<RootCategory> categories = new ArrayList<>();
				for (int i=0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
					if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
						categories.add(PocketAccounter.financeManager.getCategories().get(i));
				}
				((PocketAccounter) getActivity()).replaceFragment(new RootCategoryEditFragment(categories.get(position), PocketAccounterGeneral.NO_MODE, 0, null));
			}
			else if (chbCatIncomes.isChecked() && !chbCatExpanses.isChecked()) {
				ArrayList<RootCategory> categories = new ArrayList<>();
				for (int i=0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
					if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
						categories.add(PocketAccounter.financeManager.getCategories().get(i));
				}
				((PocketAccounter) getActivity()).replaceFragment(new RootCategoryEditFragment(categories.get(position), PocketAccounterGeneral.NO_MODE, 0, null));
			}
		}
		else {
			CheckBox chbCatListItem = (CheckBox) view.findViewById(R.id.chbAccountListItem);
			chbCatListItem.setChecked(!chbCatListItem.isChecked());
			selected[position] = chbCatListItem.isChecked();
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		refreshList(mode);
	}
	private void setMode(int mode) {
		if (mode == PocketAccounterGeneral.NORMAL_MODE) {
			selected = null;
			ivToolbarMostRight.setImageResource(R.drawable.pencil);
		}
		else {
			selected = new boolean[PocketAccounter.financeManager.getCategories().size()];
			ivToolbarMostRight.setImageResource(R.drawable.ic_delete_black);
		}
		refreshList(mode);
	}
	private void deleteCategories() {
		//delete from all categories
		if (chbCatIncomes.isChecked() && chbCatExpanses.isChecked()) {
			for (int i=0; i<selected.length; i++) {
				if (selected[i]) {
					String id = PocketAccounter.financeManager.getCategories().get(i).getId();
					for (int j=0; j<PocketAccounter.financeManager.getExpanses().size(); j++) {
						if (PocketAccounter.financeManager.getExpanses().get(j) == null) continue;
						if (PocketAccounter.financeManager.getExpanses().get(j).getId().matches(id))
							PocketAccounter.financeManager.getExpanses().set(j, null);
					}
					for (int j=0; j<PocketAccounter.financeManager.getIncomes().size(); j++) {
						if (PocketAccounter.financeManager.getIncomes().get(j) == null)	continue;
						if (PocketAccounter.financeManager.getIncomes().get(j).getId().matches(id))
							PocketAccounter.financeManager.getIncomes().set(j, null);
					}
					for (int j=0; j<PocketAccounter.financeManager.getRecords().size(); j++) {
						if (PocketAccounter.financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
							PocketAccounter.financeManager.getRecords().remove(j);
							j--;
						}
					}
					PocketAccounter.financeManager.getCategories().set(i, null);
				}
			}
		} else if (chbCatIncomes.isChecked() && !chbCatExpanses.isChecked()) {
			ArrayList<RootCategory> categories = new ArrayList<>();
			for (int i=0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
				if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.INCOME)
					categories.add(PocketAccounter.financeManager.getCategories().get(i));
			}
			for (int i=0; i<selected.length; i++) {
				if (selected[i]) {
					String id = categories.get(i).getId();
					for (int j=0; j<PocketAccounter.financeManager.getIncomes().size(); j++) {
						if (PocketAccounter.financeManager.getIncomes().get(j) == null)	continue;
						if (PocketAccounter.financeManager.getIncomes().get(j).getId().matches(id))
							PocketAccounter.financeManager.getIncomes().set(j, null);
					}
					for (int j=0; j<PocketAccounter.financeManager.getRecords().size(); j++) {
						if (PocketAccounter.financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
							PocketAccounter.financeManager.getRecords().remove(j);
							j--;
						}
					}
					for (int j=0; j<PocketAccounter.financeManager.getCategories().size(); j++) {
						if (PocketAccounter.financeManager.getCategories().get(j)!= null && PocketAccounter.financeManager.getCategories().get(j).getId().matches(id)) {
							PocketAccounter.financeManager.getCategories().set(j, null);
							break;
						}

					}
				}
			}
		} else if (!chbCatIncomes.isChecked() && chbCatExpanses.isChecked()) {
			ArrayList<RootCategory> categories = new ArrayList<>();
			for (int i=0; i < PocketAccounter.financeManager.getCategories().size(); i++) {
				if (PocketAccounter.financeManager.getCategories().get(i).getType() == PocketAccounterGeneral.EXPENSE)
					categories.add(PocketAccounter.financeManager.getCategories().get(i));
			}
			for (int i=0; i<selected.length; i++) {
				if (selected[i]) {
					String id = categories.get(i).getId();
					for (int j=0; j<PocketAccounter.financeManager.getExpanses().size(); j++) {
						if (PocketAccounter.financeManager.getExpanses().get(j) == null)	continue;
						if (PocketAccounter.financeManager.getExpanses().get(j).getId().matches(id))
							PocketAccounter.financeManager.getExpanses().set(j, null);
					}
					for (int j=0; j<PocketAccounter.financeManager.getRecords().size(); j++) {
						if (PocketAccounter.financeManager.getRecords().get(j).getCategory().getId().matches(id)) {
							PocketAccounter.financeManager.getRecords().remove(j);
							j--;
						}
					}
					for (int j=0; j<PocketAccounter.financeManager.getCategories().size(); j++) {
						if (PocketAccounter.financeManager.getCategories().get(j) != null && PocketAccounter.financeManager.getCategories().get(j).getId().matches(id))
							PocketAccounter.financeManager.getCategories().set(j, null);
					}
				}
			}
		}
		for (int i = 0; i< PocketAccounter.financeManager.getCategories().size(); i++) {
			if (PocketAccounter.financeManager.getCategories().get(i) == null) {
				PocketAccounter.financeManager.getCategories().remove(i);
				i--;
			}
		}
	}
	@Override
	public void onStop() {
		super.onStop();
		PocketAccounter.financeManager.saveIncomes();
		PocketAccounter.financeManager.saveExpenses();
		PocketAccounter.financeManager.saveCategories();
	}
}