package com.assessgo.frontend.components.navigation.bar;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;
import com.assessgo.MainView;
import com.assessgo.backend.common.ApplicationContextProvider;
import com.assessgo.backend.entity.User;
import com.assessgo.backend.repository.UserRepository;
import com.assessgo.backend.security.SecurityUtils;
import com.assessgo.backend.service.UserService;
import com.assessgo.frontend.components.FlexBoxLayout;
import com.assessgo.frontend.components.navigation.tab.NaviTab;
import com.assessgo.frontend.components.navigation.tab.NaviTabs;
import com.assessgo.frontend.util.LumoStyles;
import com.assessgo.frontend.util.UIUtils;
import com.assessgo.frontend.util.css.FlexDirection;
import com.assessgo.frontend.views.dashboard.DashboardView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static com.assessgo.frontend.util.UIUtils.IMG_PATH;


@CssImport("./styles/components/app-bar.css")
public class AppBar extends FlexBoxLayout {

	private String CLASS_NAME = "app-bar";

	private FlexBoxLayout container;

	private Button menuIcon;
	private Button contextIcon;

	private H4 title;
	private FlexBoxLayout actionItems;
	private Image avatar;

	private FlexBoxLayout tabContainer;
	private NaviTabs tabs;
	private ArrayList<Registration> tabSelectionListeners;
	private Button addTab;

	private TextField search;
	private Registration searchRegistration;

	private Dialog changePasswordDialog;

	public enum NaviMode {
		MENU, CONTEXTUAL
	}

	public AppBar(String title, NaviTab... tabs) {
		setClassName(CLASS_NAME);

		initMenuIcon();
		initContextIcon();
		initTitle(title);
		initSearch();
		initAvatar();
		initActionItems();
		initContainer();
		initTabs(tabs);
	}

	public void setNaviMode(NaviMode mode) {
		if (mode.equals(NaviMode.MENU)) {
			menuIcon.setVisible(true);
			contextIcon.setVisible(false);
		} else {
			menuIcon.setVisible(false);
			contextIcon.setVisible(true);
		}
	}

	private void initMenuIcon() {
		menuIcon = UIUtils.createTertiaryInlineButton(VaadinIcon.MENU);
		menuIcon.addClassName(CLASS_NAME + "__navi-icon");
		menuIcon.addClickListener(e -> MainView.get().getNaviDrawer().toggle());
		UIUtils.setAriaLabel("Menu", menuIcon);
		UIUtils.setLineHeight("1", menuIcon);
	}

	private void initContextIcon() {
		contextIcon = UIUtils
				.createTertiaryInlineButton(VaadinIcon.ARROW_LEFT);
		contextIcon.addClassNames(CLASS_NAME + "__context-icon");
		contextIcon.setVisible(false);
		UIUtils.setAriaLabel("Back", contextIcon);
		UIUtils.setLineHeight("1", contextIcon);
	}

	private void initTitle(String title) {
		this.title = new H4(title);
		this.title.setClassName(CLASS_NAME + "__title");
	}

	private void initSearch() {
		search = new TextField();
		search.setPlaceholder("Search");
		search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		search.setVisible(false);
	}

	private void initAvatar() {
		avatar = new Image();
		avatar.setClassName(CLASS_NAME + "__avatar");
		avatar.setSrc(IMG_PATH + "avatar.png");
		avatar.setAlt("User menu");

		ContextMenu contextMenu = new ContextMenu(avatar);
		contextMenu.setOpenOnClick(true);
		contextMenu.addItem("Update Profile",
				e -> Notification.show("Not implemented yet.", 3000,
						Notification.Position.BOTTOM_CENTER));
		contextMenu.addItem("Change Password",
				e -> changePassword());
		contextMenu.addItem("Logout",
				e -> UI.getCurrent().getPage().executeJavaScript("location.assign('logout')"));
	}

	private void initActionItems() {
		actionItems = new FlexBoxLayout();
		actionItems.addClassName(CLASS_NAME + "__action-items");
		actionItems.setVisible(false);
	}

	private void initContainer() {
		container = new FlexBoxLayout(menuIcon, contextIcon, this.title, search,
				actionItems, avatar);
		container.addClassName(CLASS_NAME + "__container");
		container.setAlignItems(FlexComponent.Alignment.CENTER);
		container.setFlexGrow(1, search);
		add(container);
	}

	private void initTabs(NaviTab... tabs) {
		addTab = UIUtils.createSmallButton(VaadinIcon.PLUS);
		addTab.addClickListener(e -> this.tabs
				.setSelectedTab(addClosableNaviTab("New Tab", DashboardView.class)));
		addTab.setVisible(false);

		this.tabs = tabs.length > 0 ? new NaviTabs(tabs) : new NaviTabs();
		this.tabs.setClassName(CLASS_NAME + "__tabs");
		this.tabs.setVisible(false);
		for (NaviTab tab : tabs) {
			configureTab(tab);
		}

		this.tabSelectionListeners = new ArrayList<>();

		tabContainer = new FlexBoxLayout(this.tabs, addTab);
		tabContainer.addClassName(CLASS_NAME + "__tab-container");
		tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
		add(tabContainer);
	}

	/* === MENU ICON === */

	public Button getMenuIcon() {
		return menuIcon;
	}

	/* === CONTEXT ICON === */

	public Button getContextIcon() {
		return contextIcon;
	}

	public void setContextIcon(Icon icon) {
		contextIcon.setIcon(icon);
	}

	/* === TITLE === */

	public String getTitle() {
		return this.title.getText();
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	/* === ACTION ITEMS === */

	public Component addActionItem(Component component) {
		actionItems.add(component);
		updateActionItemsVisibility();
		return component;
	}

	public Button addActionItem(VaadinIcon icon) {
		Button button = UIUtils.createButton(icon, ButtonVariant.LUMO_SMALL,
				ButtonVariant.LUMO_TERTIARY);
		addActionItem(button);
		return button;
	}

	public void removeAllActionItems() {
		actionItems.removeAll();
		updateActionItemsVisibility();
	}

	/* === AVATAR == */

	public Image getAvatar() {
		return avatar;
	}

	/* === TABS === */

	public void centerTabs() {
		tabs.addClassName(LumoStyles.Margin.Horizontal.AUTO);
	}

	private void configureTab(Tab tab) {
		tab.addClassName(CLASS_NAME + "__tab");
		updateTabsVisibility();
	}

	public Tab addTab(String text) {
		Tab tab = tabs.addTab(text);
		configureTab(tab);
		return tab;
	}

	public Tab addTab(String text,
	                  Class<? extends Component> navigationTarget) {
		Tab tab = tabs.addTab(text, navigationTarget);
		configureTab(tab);
		return tab;
	}

	public Tab addClosableNaviTab(String text,
	                              Class<? extends Component> navigationTarget) {
		Tab tab = tabs.addClosableTab(text, navigationTarget);
		configureTab(tab);
		return tab;
	}

	public Tab getSelectedTab() {
		return tabs.getSelectedTab();
	}

	public void setSelectedTab(Tab selectedTab) {
		tabs.setSelectedTab(selectedTab);
	}

	public void updateSelectedTab(String text,
	                              Class<? extends Component> navigationTarget) {
		tabs.updateSelectedTab(text, navigationTarget);
	}

	public void navigateToSelectedTab() {
		tabs.navigateToSelectedTab();
	}

	public void addTabSelectionListener(
			ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
		Registration registration = tabs.addSelectedChangeListener(listener);
		tabSelectionListeners.add(registration);
	}

	public int getTabCount() {
		return tabs.getTabCount();
	}

	public void removeAllTabs() {
		tabSelectionListeners.forEach(registration -> registration.remove());
		tabSelectionListeners.clear();
		tabs.removeAll();
		updateTabsVisibility();
	}

	/* === ADD TAB BUTTON === */

	public void setAddTabVisible(boolean visible) {
		addTab.setVisible(visible);
	}

	/* === SEARCH === */

	public void searchModeOn() {
		menuIcon.setVisible(false);
		title.setVisible(false);
		actionItems.setVisible(false);
		tabContainer.setVisible(false);

		contextIcon.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
		contextIcon.setVisible(true);
		searchRegistration = contextIcon
				.addClickListener(e -> searchModeOff());

		search.setVisible(true);
		search.focus();
	}

	public void addSearchListener(HasValue.ValueChangeListener listener) {
		search.addValueChangeListener(listener);
	}

	public void setSearchPlaceholder(String placeholder) {
		search.setPlaceholder(placeholder);
	}

	private void searchModeOff() {
		menuIcon.setVisible(true);
		title.setVisible(true);
		tabContainer.setVisible(true);

		updateActionItemsVisibility();
		updateTabsVisibility();

		contextIcon.setVisible(false);
		searchRegistration.remove();

		search.clear();
		search.setVisible(false);
	}

	/* === RESET === */

	public void reset() {
		title.setText("");
		setNaviMode(AppBar.NaviMode.MENU);
		removeAllActionItems();
		removeAllTabs();
	}

	/* === UPDATE VISIBILITY === */

	private void updateActionItemsVisibility() {
		actionItems.setVisible(actionItems.getComponentCount() > 0);
	}

	private void updateTabsVisibility() {
		tabs.setVisible(tabs.getComponentCount() > 0);
	}

	private void changePassword() {
		changePasswordDialog.open();
	}

	public Dialog createChangePasswordDialog() {
		changePasswordDialog = new Dialog();
		changePasswordDialog.addDialogCloseActionListener(e-> changePasswordDialog.close());
		changePasswordDialog.add(new H3("Change Password"));

		PasswordField txtVerifyPassword = new PasswordField("Re-enter password");
		txtVerifyPassword.setValueChangeMode(ValueChangeMode.EAGER);
		txtVerifyPassword.setWidth("100%");

		Button btnVerifyPassword = UIUtils.createPrimaryButton("Verify Password");
		btnVerifyPassword.setEnabled(false);
		btnVerifyPassword.addClassName(LumoStyles.Margin.Top.XL);


		txtVerifyPassword.addValueChangeListener(event -> {
			if (StringUtils.isNotBlank(event.getValue()) && event.getValue().length() >= 6
					&& event.getValue().length() <= 15) {
				btnVerifyPassword.setEnabled(true);
			} else {
				btnVerifyPassword.setEnabled(false);
			}
		});

		FlexBoxLayout verifyPassLayout = new FlexBoxLayout(txtVerifyPassword, btnVerifyPassword);
		verifyPassLayout.setFlexDirection(FlexDirection.COLUMN);
		verifyPassLayout.addClassNames(LumoStyles.Padding.Horizontal.XL, LumoStyles.Padding.Vertical.L);
		verifyPassLayout.setMinWidth("400px");
		verifyPassLayout.setMinHeight("300px");

		PasswordField txtPassword = new PasswordField("Enter new password");
		txtPassword.setWidth("100%");

		PasswordField txtConfirmPassword = new PasswordField("Confirm password");
		txtPassword.setWidth("100%");

		Button btnChangePassword = UIUtils.createPrimaryButton("Change");
		btnChangePassword.addClassName(LumoStyles.Margin.Top.XL);

		FlexBoxLayout changePassLayout = new FlexBoxLayout(txtPassword, txtConfirmPassword, btnChangePassword);
		changePassLayout.setFlexDirection(FlexDirection.COLUMN);
		changePassLayout.addClassNames(LumoStyles.Padding.Horizontal.XL, LumoStyles.Padding.Vertical.L);
		changePassLayout.setVisible(false);
		changePassLayout.setMinHeight("300px");
		changePassLayout.setMinWidth("400px");

		btnVerifyPassword.addClickListener(clickEvent -> {
			UserService userService = getUserService();


			PasswordEncoder encoder = new BCryptPasswordEncoder();

			User user = ((UserRepository) userService.getRepository()).
					findByEmail(SecurityUtils.getLoggedInUsername());





			if (user != null && encoder.matches(txtVerifyPassword.getValue(), user.getPasswordHash())) {
				changePassLayout.setVisible(true);
				verifyPassLayout.setVisible(false);
			} else {
				Notification.show("Please ensure you entered correct password");
			}

		});

		btnChangePassword.addClickListener(clickEvent -> {
			String password = txtPassword.getValue();
			String confirmPassword = txtConfirmPassword.getValue();

			if(validate(password, confirmPassword)){
				UserService userService = getUserService();

				try {
					userService.updatePassword(SecurityUtils.getLoggedInUsername(), password);
					changePasswordDialog.close();
					Notification.show("Your Password has been changed successfully");
				} catch (Exception e) {
					e.printStackTrace();
					Notification.show("Something went wrong while changing the password! Please try again.");
				}
			}

		});

		changePasswordDialog.add(verifyPassLayout, changePassLayout);

		changePasswordDialog.addOpenedChangeListener(event -> {
			changePassLayout.setVisible(false);
			verifyPassLayout.setVisible(true);
			txtConfirmPassword.clear();
			txtPassword.clear();
			txtVerifyPassword.clear();
		});
		return changePasswordDialog;
	}

	private boolean validate(String pass, String confirmPass) {
		boolean status = true;
		if(StringUtils.isBlank(pass) || StringUtils.isBlank(confirmPass)){
			Notification.show("Enter password and confirm password field is mandatory.");
			status = false;
		} else if((pass.length() < 6 || pass.length() > 15)
				|| (confirmPass.length() < 6 || confirmPass.length() > 15)){
			Notification.show("Invalid password! Length must be between 6 and 15.");
			status = false;
		}  else if(!pass.equals(confirmPass)){
			Notification.show("Password and Confirm password value should be same.");
			status = false;
		}
		return status;
	}

	private UserService getUserService() {
		return ApplicationContextProvider.getBean(UserService.class);
	}
}
