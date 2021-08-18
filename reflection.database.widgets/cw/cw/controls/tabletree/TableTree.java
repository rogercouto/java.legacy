package cw.controls.tabletree;
/*

 * (c) Copyright IBM Corp. 2000, 2001.

 * All Rights Reserved

 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TypedListener;

/**
 * 
 * A TableTree is a selectable user interface object
 * 
 * that displays a hierarchy of items, and issues
 * 
 * notification when an item is selected.
 * 
 * A TableTree may be single or multi select.
 * 
 * <p>
 * 
 * The item children that may be added to instances of this class
 * 
 * must be of type <code>TableTreeItem</code>.
 * 
 * </p>
 * <p>
 * 
 * Note that although this class is a subclass of <code>Composite</code>,
 * 
 * it does not make sense to add <code>Control</code> children to it,
 * 
 * or set a layout on it.
 * 
 * </p>
 * <p>
 * 
 * <dl>
 * 
 * <dt><b>Styles: </b>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION
 * 
 * <dt><b>Events: </b>
 * <dd>Selection, DefaultSelection, Collapse, Expand
 * 
 * </dl>
 *  
 */

public class TableTree extends Composite {

  Table table;

  TableTreeItem[] items = EMPTY_ITEMS;

  Image plusImage, minusImage, sizeImage;

  /*
   * 
   * TableTreeItems are not treated as children but rather as items.
   * 
   * When the TableTree is disposed, all children are disposed because
   * 
   * TableTree inherits this behaviour from Composite. The items
   * 
   * must be disposed separately. Because TableTree is not part of
   * 
   * the org.eclipse.swt.widgets package, the method releaseWidget can
   * 
   * not be overriden (this is how items are disposed of in Table and Tree).
   * 
   * Instead, the items are disposed of in response to the dispose event on
   * the
   * 
   * TableTree. The "inDispose" flag is used to distinguish between disposing
   * 
   * one TableTreeItem (e.g. when removing an entry from the TableTree) and
   * 
   * disposing the entire TableTree.
   *  
   */

  boolean inDispose = false;

  static final TableTreeItem[] EMPTY_ITEMS = new TableTreeItem[0];

  static final String[] EMPTY_TEXTS = new String[0];

  static final Image[] EMPTY_IMAGES = new Image[0];

  /**
   * 
   * Creates a new instance of the widget.
   * 
   * 
   * 
   * @param parent
   *            a composite widget
   * 
   * @param style
   *            the bitwise OR'ing of widget styles
   *  
   */

  public TableTree(Composite parent, int style) {

    super(parent, SWT.NONE);

    table = new Table(this, style);

    setBackground(table.getBackground());

    setForeground(table.getForeground());

    setFont(table.getFont());

    table.addListener(SWT.MouseDown, new Listener() {

      public void handleEvent(Event e) {

        onMouseDown(e);

      }

    });

    table.addListener(SWT.Selection, new Listener() {

      public void handleEvent(Event e) {

        onSelection(e);

      }

    });

    table.addListener(SWT.DefaultSelection, new Listener() {

      public void handleEvent(Event e) {

        onSelection(e);

      }

    });

    addListener(SWT.Dispose, new Listener() {

      public void handleEvent(Event e) {

        onDispose();

      }

    });

    addListener(SWT.Resize, new Listener() {

      public void handleEvent(Event e) {

        onResize();

      }

    });

    addListener(SWT.FocusIn, new Listener() {

      public void handleEvent(Event e) {

        onFocusIn();

      }

    });

  }

  int addItem(TableTreeItem item, int index) {

    if (index < 0 || index > items.length)
      throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);

    TableTreeItem[] newItems = new TableTreeItem[items.length + 1];

    System.arraycopy(items, 0, newItems, 0, index);

    newItems[index] = item;

    System.arraycopy(items, index, newItems, index + 1, items.length
        - index);

    items = newItems;

    /* Return the index in the table where this table should be inserted */

    if (index == items.length - 1)

      return table.getItemCount();

    else

      return table.indexOf(items[index + 1].tableItem);

  }

  /**
   * 
   * Adds the listener to receive selection events.
   * 
   * <p>
   * 
   * 
   * 
   * @param listener
   *            the selection listener
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when listener is null
   * 
   * </ul>
   *  
   */

  public void addSelectionListener(SelectionListener listener) {

    if (listener == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    TypedListener typedListener = new TypedListener(listener);

    addListener(SWT.Selection, typedListener);

    addListener(SWT.DefaultSelection, typedListener);

  }

  /**
   * 
   * Adds the listener to receive tree events.
   * 
   * <p>
   * 
   * 
   * 
   * @param listener
   *            the tree listener
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when listener is null
   * 
   * </ul>
   *  
   */

  public void addTreeListener(TreeListener listener) {

    if (listener == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    TypedListener typedListener = new TypedListener(listener);

    addListener(SWT.Expand, typedListener);

    addListener(SWT.Collapse, typedListener);

  }

  /**
   * 
   * Computes the preferred size of the widget.
   * 
   * <p>
   * 
   * Calculate the preferred size of the widget based
   * 
   * on the current contents. The hint arguments allow
   * 
   * a specific client area width and/or height to be
   * 
   * requested. The hints may be honored depending on
   * 
   * the platform and the layout.
   * 
   * 
   * 
   * @param wHint
   *            the width hint (can be SWT.DEFAULT)
   * 
   * @param hHint
   *            the height hint (can be SWT.DEFAULT)
   * 
   * @return a point containing the preferred size of the widget including
   *         trim
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public Point computeSize(int wHint, int hHint) {

    return table.computeSize(wHint, hHint, true);

  }

  /**
   * 
   * Computes the widget trim.
   * 
   * <p>
   * 
   * Trim is widget specific and may include scroll
   * 
   * bars and menu bar in addition to other trimmings
   * 
   * that are outside of the widget's client area.
   * 
   * 
   * 
   * @param x
   *            the x location of the client area
   * 
   * @param y
   *            the y location of the client area
   * 
   * @param width
   *            the width of the client area
   * 
   * @param height
   *            the height of the client area
   * 
   * @return a rectangle containing the trim of the widget.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public Rectangle computeTrim(int x, int y, int width, int height) {

    return table.computeTrim(x, y, width, height);

  }

  /**
   * 
   * Deselects all items.
   * 
   * <p>
   * 
   * If an item is selected, it is deselected.
   * 
   * If an item is not selected, it remains unselected.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * </ul>
   *  
   */

  public void deselectAll() {

    table.deselectAll();

  }

  /* Expand upward from the specified leaf item. */

  void expandItem(TableTreeItem item) {

    if (item == null || item.getExpanded())
      return;

    expandItem(item.parentItem);

    item.setExpanded(true);

    Event event = new Event();

    event.item = item;

    notifyListeners(SWT.Expand, event);

  }

  /**
   * 
   * Gets the number of items.
   * 
   * <p>
   * 
   * @return the number of items in the widget
   *  
   */

  public int getItemCount() {

    return items.length;

  }

  public void setHeaderVisible(boolean show){
	  table.setHeaderVisible(show);
  }
  
  public boolean isHeaderVisible(){
	  return table.getHeaderVisible();
  }
  /**
   * 
   * Gets the height of one item.
   * 
   * <p>
   * 
   * This operation will fail if the height of
   * 
   * one item could not be queried from the OS.
   * 
   * 
   * 
   * @return the height of one item in the widget
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_CANNOT_GET_ITEM_HEIGHT when the operation fails
   * 
   * </ul>
   *  
   */

  public int getItemHeight() {

    return table.getItemHeight();

  }

  /**
   * 
   * Gets the items.
   * 
   * <p>
   * 
   * @return the items in the widget
   * 
   * 
   *  
   */

  public TableTreeItem[] getItems() {

    TableTreeItem[] newItems = new TableTreeItem[items.length];

    System.arraycopy(items, 0, newItems, 0, items.length);

    return newItems;

  }

  /**
   * 
   * Gets the selected items.
   * 
   * <p>
   * 
   * This operation will fail if the selected
   * 
   * items cannot be queried from the OS.
   * 
   * 
   * 
   * @return the selected items in the widget
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_CANNOT_GET_SELECTION when the operation fails</li>
   * 
   * </ul>
   *  
   */

  public TableTreeItem[] getSelection() {

    TableItem[] selection = table.getSelection();

    TableTreeItem[] result = new TableTreeItem[selection.length];

    for (int i = 0; i < selection.length; i++) {

      result[i] = (TableTreeItem) selection[i].getData();

    }

    return result;

  }

  /**
   * 
   * Gets the number of selected items.
   * 
   * <p>
   * 
   * This operation will fail if the number of selected
   * 
   * items cannot be queried from the OS.
   * 
   * 
   * 
   * @return the number of selected items in the widget
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_CANNOT_GET_COUNT when the operation fails</li>
   * 
   * </ul>
   *  
   */

  public int getSelectionCount() {

    return table.getSelectionCount();

  }

  /**
   * 
   * Returns the underlying Table control.
   * 
   * 
   * 
   * @return the underlying Table control
   *  
   */

  public Table getTable() {

    return table;

  }

  void createImages() {

    int itemHeight = sizeImage.getBounds().height;

    // Calculate border around image.

    // At least 9 pixels are needed to draw the image

    // Leave at least a 6 pixel border.

    int indent = Math.min(6, (itemHeight - 9) / 2);

    indent = Math.max(0, indent);

    int size = Math.max(10, itemHeight - 2 * indent);

    size = ((size + 1) / 2) * 2; // size must be an even number

    int midpoint = indent + size / 2;

    Color foreground = getForeground();

    Color plusMinus = getDisplay().getSystemColor(
        SWT.COLOR_WIDGET_NORMAL_SHADOW);

    Color background = getBackground();

    /* Plus image */

    PaletteData palette = new PaletteData(new RGB[] { foreground.getRGB(),
        background.getRGB(), plusMinus.getRGB() });

    ImageData imageData = new ImageData(itemHeight, itemHeight, 4, palette);

    imageData.transparentPixel = 1;

    plusImage = new Image(getDisplay(), imageData);

    GC gc = new GC(plusImage);

    gc.setBackground(background);

    gc.fillRectangle(0, 0, itemHeight, itemHeight);

    gc.setForeground(plusMinus);

    gc.drawRectangle(indent, indent, size, size);

    gc.setForeground(foreground);

    gc.drawLine(midpoint, indent + 2, midpoint, indent + size - 2);

    gc.drawLine(indent + 2, midpoint, indent + size - 2, midpoint);

    gc.dispose();

    /* Minus image */

    palette = new PaletteData(new RGB[] { foreground.getRGB(),
        background.getRGB(), plusMinus.getRGB() });

    imageData = new ImageData(itemHeight, itemHeight, 4, palette);

    imageData.transparentPixel = 1;

    minusImage = new Image(getDisplay(), imageData);

    gc = new GC(minusImage);

    gc.setBackground(background);

    gc.fillRectangle(0, 0, itemHeight, itemHeight);

    gc.setForeground(plusMinus);

    gc.drawRectangle(indent, indent, size, size);

    gc.setForeground(foreground);

    gc.drawLine(indent + 2, midpoint, indent + size - 2, midpoint);

    gc.dispose();

  }

  Image getPlusImage() {

    if (plusImage == null)
      createImages();

    return plusImage;

  }

  Image getMinusImage() {

    if (minusImage == null)
      createImages();

    return minusImage;

  }

  /**
   * 
   * Gets the index of an item.
   * 
   * 
   * 
   * <p>
   * The widget is searched starting at 0 until an
   * 
   * item is found that is equal to the search item.
   * 
   * If no item is found, -1 is returned. Indexing
   * 
   * is zero based. This index is relative to the parent only.
   * 
   * 
   * 
   * @param item
   *            the search item
   * 
   * @return the index of the item or -1
   * 
   * 
   *  
   */

  public int indexOf(TableTreeItem item) {

    for (int i = 0; i < items.length; i++) {

      if (item == items[i])
        return i;

    }

    return -1;

  }

  void onDispose() {

    inDispose = true;

    for (int i = 0; i < items.length; i++) {

      items[i].dispose();

    }

    inDispose = false;

    if (plusImage != null)
      plusImage.dispose();

    if (minusImage != null)
      minusImage.dispose();

    if (sizeImage != null)
      sizeImage.dispose();

    plusImage = minusImage = sizeImage = null;

  }

  void onResize() {

    Rectangle area = getClientArea();

    table.setBounds(0, 0, area.width, area.height);

  }

  void onSelection(Event e) {

    Event event = new Event();

    TableItem tableItem = (TableItem) e.item;

    TableTreeItem item = getItem(tableItem);

    event.item = item;

    if (e.type == SWT.Selection

    && e.detail == SWT.CHECK

    && item != null) {

      event.detail = SWT.CHECK;

      item.checked = tableItem.getChecked();

    }

    notifyListeners(e.type, event);

  }

  public TableTreeItem getItem(Point point) {

    TableItem item = table.getItem(point);

    if (item == null)
      return null;

    return getItem(item);

  }

  TableTreeItem getItem(TableItem tableItem) {

    if (tableItem == null)
      return null;

    for (int i = 0; i < items.length; i++) {

      TableTreeItem item = items[i].getItem(tableItem);

      if (item != null)
        return item;

    }

    return null;

  }

  void onFocusIn() {

    table.setFocus();

  }

  void onMouseDown(Event event) {

    /* If user clicked on the [+] or [-], expand or collapse the tree. */

    TableItem[] items = table.getItems();

    for (int i = 0; i < items.length; i++) {

      Rectangle rect = items[i].getImageBounds(0);

      if (rect.contains(event.x, event.y)) {

        TableTreeItem item = (TableTreeItem) items[i].getData();

        event = new Event();

        event.item = item;

        item.setExpanded(!item.getExpanded());

        if (item.getExpanded()) {

          notifyListeners(SWT.Expand, event);

        } else {

          notifyListeners(SWT.Collapse, event);

        }

        return;

      }

    }

  }

  /**
   * 
   * Removes all items.
   * 
   * <p>
   * 
   * This operation will fail when an item
   * 
   * could not be removed in the OS.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_ITEM_NOT_REMOVED when the operation fails
   * 
   * </ul>
   *  
   */

  public void removeAll() {

    setRedraw(false);

    for (int i = items.length - 1; i >= 0; i--) {

      items[i].dispose();

    }

    items = EMPTY_ITEMS;

    setRedraw(true);

  }

  void removeItem(TableTreeItem item) {

    int index = 0;

    while (index < items.length && items[index] != item)
      index++;

    if (index == items.length)
      return;

    TableTreeItem[] newItems = new TableTreeItem[items.length - 1];

    System.arraycopy(items, 0, newItems, 0, index);

    System.arraycopy(items, index + 1, newItems, index, items.length
        - index - 1);

    items = newItems;

  }

  /**
   * 
   * Removes the listener.
   * 
   * <p>
   * 
   * 
   * 
   * @param listener
   *            the listener
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when listener is null
   * 
   * </ul>
   *  
   */

  public void removeSelectionListener(SelectionListener listener) {

    if (listener == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    removeListener(SWT.Selection, listener);

    removeListener(SWT.DefaultSelection, listener);

  }

  /**
   * 
   * Removes the listener.
   * 
   * 
   * 
   * @param listener
   *            the listener
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when listener is null
   * 
   * </ul>
   *  
   */

  public void removeTreeListener(TreeListener listener) {

    if (listener == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    removeListener(SWT.Expand, listener);

    removeListener(SWT.Collapse, listener);

  }

  /**
   * 
   * Selects all items.
   * 
   * <p>
   * 
   * If an item is not selected, it is selected.
   * 
   * If an item is selected, it remains selected.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * </ul>
   *  
   */

  public void selectAll() {

    table.selectAll();

  }

  /**
   * 
   * Sets the widget background color.
   * 
   * <p>
   * 
   * When new color is null, the background reverts
   * 
   * to the default system color for the widget.
   * 
   * 
   * 
   * @param color
   *            the new color (or null)
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setBackground(Color color) {

    super.setBackground(color);

    table.setBackground(color);

    if (sizeImage != null) {

      GC gc = new GC(sizeImage);

      gc.setBackground(getBackground());

      Rectangle size = sizeImage.getBounds();

      gc.fillRectangle(size);

      gc.dispose();

    }

  }

  /**
   * 
   * Sets the enabled state.
   * 
   * <p>
   * 
   * A disabled widget is typically not selectable from
   * 
   * the user interface and draws with an inactive or
   * 
   * grayed look.
   * 
   * 
   * 
   * @param enabled
   *            the new enabled state
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setEnabled(boolean enabled) {

    super.setEnabled(enabled);

    table.setEnabled(enabled);

  }

  /**
   * 
   * Sets the widget font.
   * 
   * <p>
   * 
   * When new font is null, the font reverts
   * 
   * to the default system font for the widget.
   * 
   * 
   * 
   * @param font
   *            the new font (or null)
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setFont(Font font) {

    super.setFont(font);

    table.setFont(font);

  }

  /**
   * 
   * Gets the widget foreground color.
   * 
   * <p>
   * 
   * @return the widget foreground color
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setForeground(Color color) {

    super.setForeground(color);

    table.setForeground(color);

  }

  /**
   * 
   * Sets the pop up menu.
   * 
   * <p>
   * 
   * Every control has an optional pop up menu that is
   * 
   * displayed when the user requests a popup menu for
   * 
   * the control. The sequence of key strokes/button
   * 
   * presses/button releases that is used to request
   * 
   * a pop up menu is platform specific.
   * 
   * 
   * 
   * @param menu
   *            the new pop up menu
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_MENU_NOT_POP_UP when the menu is not a POP_UP</li>
   * 
   * <li>ERROR_NO_COMMON_PARENT when the menu is not in the same widget tree
   * </li>
   * 
   * </ul>
   *  
   */

  public void setMenu(Menu menu) {

    super.setMenu(menu);

    table.setMenu(menu);

  }

  /**
   * 
   * Sets the selection.
   * 
   * <p>
   * 
   * @param items
   *            new selection
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when items is null
   * 
   * </ul>
   *  
   */

  public void setSelection(TableTreeItem[] items) {

    TableItem[] tableItems = new TableItem[items.length];

    for (int i = 0; i < items.length; i++) {

      if (items[i] == null)
        throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

      if (!items[i].getVisible())
        expandItem(items[i]);

      tableItems[i] = items[i].tableItem;

    }

    table.setSelection(tableItems);

  }

  /**
   * 
   * Sets the tool tip text.
   * 
   * <p>
   * 
   * @param string
   *            the new tool tip text (or null)
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setToolTipText(String string) {

    super.setToolTipText(string);

    table.setToolTipText(string);

  }

  /**
   * 
   * Shows the item.
   * 
   * <p>
   * 
   * @param item
   *            the item to be shown
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * <li>ERROR_NULL_ARGUMENT when item is null
   * 
   * </ul>
   *  
   */

  public void showItem(TableTreeItem item) {

    if (item == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    if (!item.getVisible())
      expandItem(item);

    TableItem tableItem = item.tableItem;

    table.showItem(tableItem);

  }

  /**
   * 
   * Shows the selection.
   * 
   * <p>
   * 
   * If there is no selection or the selection
   * 
   * is already visible, this method does nothing.
   * 
   * If the selection is scrolled out of view,
   * 
   * the top index of the widget is changed such
   * 
   * that selection becomes visible.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed
   * 
   * </ul>
   *  
   */

  public void showSelection() {

    table.showSelection();

  }

}

//TableTreeItem

/*
 * 
 * (c) Copyright IBM Corp. 2000, 2001.
 * 
 * All Rights Reserved
 *  
 */

/**
 * 
 * A TableTreeItem is a selectable user interface object
 * 
 * that represents an item in a heirarchy of items in a
 * 
 * TableTree.
 *  
 */

class TableTreeItem extends Item {

  TableItem tableItem;

  TableTree parent;

  TableTreeItem parentItem;

  TableTreeItem[] items = TableTree.EMPTY_ITEMS;

  String[] texts = TableTree.EMPTY_TEXTS;

  Image[] images = TableTree.EMPTY_IMAGES;

  boolean expanded;

  boolean checked;

  /**
   * 
   * Create a new instance of a root item.
   * 
   * 
   * 
   * @param parent
   *            the TableTree that contains this root item
   * 
   * @param style
   *            the bitwise OR'ing of widget styles
   *  
   */

  public TableTreeItem(TableTree parent, int style) {

    this(parent, style, parent.getItemCount());

  }

  /**
   * 
   * Create a new instance of a root item in the position
   * 
   * indicated by the specified index.
   * 
   * 
   * 
   * @param parent
   *            the TableTree that contains this root item
   * 
   * @param style
   *            the bitwise OR'ing of widget styles
   * 
   * @param index
   *            specifies the position of this item in the TableTree
   * 
   * relative to other root items
   *  
   */

  public TableTreeItem(TableTree parent, int style, int index) {

    this(parent, null, style, index);

  }

  /**
   * 
   * Create a new instance of a sub item.
   * 
   * 
   * 
   * @param parent
   *            this item's parent in the hierarchy of TableTree items
   * 
   * @param style
   *            the bitwise OR'ing of widget styles
   *  
   */

  public TableTreeItem(TableTreeItem parent, int style) {

    this(parent, style, parent.getItemCount());

  }

  /**
   * 
   * Create a new instance of a sub item in the position
   * 
   * indicated by the specified index.
   * 
   * 
   * 
   * @param parent
   *            this item's parent in the hierarchy of TableTree items
   * 
   * @param style
   *            the bitwise OR'ing of widget styles
   * 
   * @param index
   *            specifies the position of this item in the TableTree
   * 
   * relative to other children of the same parent
   *  
   */

  public TableTreeItem(TableTreeItem parent, int style, int index) {

    this(parent.getParent(), parent, style, index);

  }

  TableTreeItem(TableTree parent, TableTreeItem parentItem, int style,
      int index) {

    super(parent, style);

    this.parent = parent;

    this.parentItem = parentItem;

    if (parentItem == null) {

      /* Root items are visible immediately */

      int tableIndex = parent.addItem(this, index);

      tableItem = new TableItem(parent.getTable(), style, tableIndex);

      tableItem.setData(this);

      addCheck();

      /*
       * 
       * Feature in the Table. The table uses the first image that
       * 
       * is inserted into the table to size the table rows. If the
       * 
       * user is allowed to insert the first image, this will cause
       * 
       * the +/- images to be scaled. The fix is to insert a dummy
       * 
       * image to force the size.
       *  
       */

      if (parent.sizeImage == null) {

        int itemHeight = parent.getItemHeight();

        parent.sizeImage = new Image(null, itemHeight, itemHeight);

        GC gc = new GC(parent.sizeImage);

        gc.setBackground(parent.getBackground());

        gc.fillRectangle(0, 0, itemHeight, itemHeight);

        gc.dispose();

        tableItem.setImage(0, parent.sizeImage);

      }

    } else {

      parentItem.addItem(this, index);

    }

  }

  void addCheck() {

    Table table = parent.getTable();

    if ((table.getStyle() & SWT.CHECK) == 0)
      return;

    tableItem.setChecked(checked);

  }

  void addItem(TableTreeItem item, int index) {

    if (item == null)
      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);

    if (index < 0 || index > items.length)
      throw new SWTError(SWT.ERROR_INVALID_ARGUMENT);

    /* Now that item has a sub-node it must indicate that it can be expanded */

    if (items.length == 0 && index == 0) {

      if (tableItem != null) {

        Image image = expanded ? parent.getMinusImage() : parent
            .getPlusImage();

        tableItem.setImage(0, image);

      }

    }

    /* Put the item in the items list */

    TableTreeItem[] newItems = new TableTreeItem[items.length + 1];

    System.arraycopy(items, 0, newItems, 0, index);

    newItems[index] = item;

    System.arraycopy(items, index, newItems, index + 1, items.length
        - index);

    items = newItems;

    if (expanded)
      item.setVisible(true);

  }

  /**
   * 
   * Gets the widget bounds at the specified index.
   * 
   * <p>
   * 
   * @return the widget bounds at the specified index
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public Rectangle getBounds(int index) {

    if (tableItem != null) {

      return tableItem.getBounds(index);

    } else {

      return new Rectangle(0, 0, 0, 0);

    }

  }

  /**
   * 
   * Gets the checked state.
   * 
   * <p>
   * 
   * @return the item checked state.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public boolean getChecked() {

    if (tableItem == null) {

      return checked;

    }

    return tableItem.getChecked();

  }

  /**
   * 
   * Gets the Display.
   * 
   * <p>
   * 
   * This method gets the Display that is associated
   * 
   * with the widget.
   * 
   * 
   * 
   * @return the widget data
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public Display getDisplay() {

    TableTree parent = this.parent;

    if (parent == null)
      throw new SWTError(SWT.ERROR_WIDGET_DISPOSED);

    return parent.getDisplay();

  }

  /**
   * 
   * Gets the expanded state of the widget.
   * 
   * <p>
   * 
   * @return a boolean that is the expanded state of the widget
   *  
   */

  public boolean getExpanded() {

    return expanded;

  }

  /**
   * 
   * Gets the first image.
   * 
   * <p>
   * 
   * The image in column 0 is reserved for the [+] and [-]
   * 
   * images of the tree, therefore getImage(0) will return null.
   * 
   * 
   * 
   * @return the image at index 0
   *  
   */

  public Image getImage() {

    return getImage(0);

  }

  /**
   * 
   * Gets the image at the specified index.
   * 
   * <p>
   * 
   * Indexing is zero based. The image can be null.
   * 
   * The image in column 0 is reserved for the [+] and [-]
   * 
   * images of the tree, therefore getImage(0) will return null.
   * 
   * Return null if the index is out of range.
   * 
   * 
   * 
   * @param index
   *            the index of the image
   * 
   * @return the image at the specified index or null
   *  
   */

  public Image getImage(int index) {

    if (0 < index && index < images.length)
      return images[index];

    return null;

  }

  int getIndent() {

    if (parentItem == null)
      return 0;

    return parentItem.getIndent() + 1;

  }

  /**
   * 
   * Gets the number of sub items.
   * 
   * <p>
   * 
   * @return the number of sub items
   *  
   */

  public int getItemCount() {

    return items.length;

  }

  /**
   * 
   * Gets the sub items.
   * 
   * <p>
   * 
   * @return the sub items
   *  
   */

  public TableTreeItem[] getItems() {

    TableTreeItem[] newItems = new TableTreeItem[items.length];

    System.arraycopy(items, 0, newItems, 0, items.length);

    return newItems;

  }

  TableTreeItem getItem(TableItem tableItem) {

    if (tableItem == null)
      return null;

    if (this.tableItem == tableItem)
      return this;

    for (int i = 0; i < items.length; i++) {

      TableTreeItem item = items[i].getItem(tableItem);

      if (item != null)
        return item;

    }

    return null;

  }

  /**
   * 
   * Gets the parent.
   * 
   * <p>
   * 
   * @return the parent
   *  
   */

  public TableTree getParent() {

    return parent;

  }

  /**
   * 
   * Gets the parent item.
   * 
   * <p>
   * 
   * @return the parent item.
   *  
   */

  public TableTreeItem getParentItem() {

    return parentItem;

  }

  /**
   * 
   * Gets the first item text.
   * 
   * <p>
   * 
   * @return the item text at index 0, which can be null
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_CANNOT_GET_TEXT when the operation fails</li>
   * 
   * </ul>
   *  
   */

  public String getText() {

    return getText(0);

  }

  /**
   * 
   * Gets the item text at the specified index.
   * 
   * <p>
   * 
   * Indexing is zero based.
   * 
   * 
   * 
   * This operation will fail when the index is out
   * 
   * of range or an item could not be queried from
   * 
   * the OS.
   * 
   * 
   * 
   * @param index
   *            the index of the item
   * 
   * @return the item text at the specified index, which can be null
   *  
   */

  public String getText(int index) {

    if (0 <= index && index < texts.length)
      return texts[index];

    return null;

  }

  boolean getVisible() {

    return tableItem != null;

  }

  /**
   * 
   * Gets the index of the specified item.
   * 
   * 
   * 
   * <p>
   * The widget is searched starting at 0 until an
   * 
   * item is found that is equal to the search item.
   * 
   * If no item is found, -1 is returned. Indexing
   * 
   * is zero based. This index is relative to the parent only.
   * 
   * 
   * 
   * @param item
   *            the search item
   * 
   * @return the index of the item or -1 if the item is not found
   * 
   * 
   *  
   */

  public int indexOf(TableTreeItem item) {

    for (int i = 0; i < items.length; i++) {

      if (items[i] == item)
        return i;

    }

    return -1;

  }

  int expandedIndexOf(TableTreeItem item) {

    int index = 0;

    for (int i = 0; i < items.length; i++) {

      if (items[i] == item)
        return index;

      if (items[i].expanded)
        index += items[i].visibleChildrenCount();

      index++;

    }

    return -1;

  }

  int visibleChildrenCount() {

    int count = 0;

    for (int i = 0; i < items.length; i++) {

      if (items[i].getVisible()) {

        count += 1 + items[i].visibleChildrenCount();

      }

    }

    return count;

  }

  public void dispose() {

    for (int i = items.length - 1; i >= 0; i--) {

      items[i].dispose();

    }

    super.dispose();

    if (!parent.inDispose) {

      if (parentItem != null) {

        parentItem.removeItem(this);

      } else {

        parent.removeItem(this);

      }

      if (tableItem != null)
        tableItem.dispose();

    }

    items = null;

    parentItem = null;

    parent = null;

    images = null;

    texts = null;

    tableItem = null;

  }

  void removeItem(TableTreeItem item) {

    int index = 0;

    while (index < items.length && items[index] != item)
      index++;

    if (index == items.length)
      return;

    TableTreeItem[] newItems = new TableTreeItem[items.length - 1];

    System.arraycopy(items, 0, newItems, 0, index);

    System.arraycopy(items, index + 1, newItems, index, items.length
        - index - 1);

    items = newItems;

    if (items.length == 0) {

      if (tableItem != null)
        tableItem.setImage(0, null);

    }

  }

  /**
   * 
   * Sets the checked state.
   * 
   * <p>
   * 
   * @param checked
   *            the new checked state.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setChecked(boolean checked) {

    if (tableItem != null) {

      tableItem.setChecked(checked);

    }

    this.checked = checked;

  }

  /**
   * 
   * Sets the expanded state.
   * 
   * <p>
   * 
   * @param expanded
   *            the new expanded state.
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setExpanded(boolean expanded) {

    if (items.length == 0)
      return;

    this.expanded = expanded;

    if (tableItem == null)
      return;

    parent.setRedraw(false);

    for (int i = 0; i < items.length; i++) {

      items[i].setVisible(expanded);

    }

    Image image = expanded ? parent.getMinusImage() : parent.getPlusImage();

    tableItem.setImage(0, image);

    parent.setRedraw(true);

  }

  /**
   * 
   * Sets the image at an index.
   * 
   * <p>
   * 
   * The image can be null.
   * 
   * The image in column 0 is reserved for the [+] and [-]
   * 
   * images of the tree, therefore do nothing if index is 0.
   * 
   * 
   * 
   * @param image
   *            the new image or null
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * </ul>
   *  
   */

  public void setImage(int index, Image image) {

    int columnCount = Math.max(parent.getTable().getColumnCount(), 1);

    if (index <= 0 || index >= columnCount)
      return;

    if (images.length < columnCount) {

      Image[] newImages = new Image[columnCount];

      System.arraycopy(images, 0, newImages, 0, images.length);

      images = newImages;

    }

    images[index] = image;

    if (tableItem != null)
      tableItem.setImage(index, image);

  }

  /**
   * 
   * Sets the first image.
   * 
   * <p>
   * 
   * The image can be null.
   * 
   * The image in column 0 is reserved for the [+] and [-]
   * 
   * images of the tree, therefore do nothing.
   * 
   * 
   * 
   * @param image
   *            the new image or null
   *  
   */

  public void setImage(Image image) {

    setImage(0, image);

  }

  /**
   * 
   * Sets the widget text.
   * 
   * <p>
   * 
   * 
   * 
   * The widget text for an item is the label of the
   * 
   * item or the label of the text specified by a column
   * 
   * number.
   * 
   * 
   * 
   * @param index
   *            the column number
   * 
   * @param text
   *            the new text
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_NULL_ARGUMENT when string is null</li>
   * 
   * </ul>
   *  
   */

  public void setText(int index, String text) {

    int columnCount = Math.max(parent.getTable().getColumnCount(), 1);

    if (index < 0 || index >= columnCount)
      return;

    if (texts.length < columnCount) {

      String[] newTexts = new String[columnCount];

      System.arraycopy(texts, 0, newTexts, 0, texts.length);

      texts = newTexts;

    }

    texts[index] = text;

    if (tableItem != null)
      tableItem.setText(index, text);

  }

  /**
   * 
   * Sets the widget text.
   * 
   * <p>
   * 
   * 
   * 
   * The widget text for an item is the label of the
   * 
   * item or the label of the text specified by a column
   * 
   * number.
   * 
   * 
   * 
   * @param index
   *            the column number
   * 
   * @param text
   *            the new text
   * 
   * 
   * 
   * @exception SWTError
   *                <ul>
   * 
   * <li>ERROR_THREAD_INVALID_ACCESS when called from the wrong thread</li>
   * 
   * <li>ERROR_WIDGET_DISPOSED when the widget has been disposed</li>
   * 
   * <li>ERROR_NULL_ARGUMENT when string is null</li>
   * 
   * </ul>
   *  
   */

  public void setText(String string) {

    setText(0, string);

  }

  @SuppressWarnings("deprecation")
void setVisible(boolean show) {

    if (parentItem == null)
      return; // this is a root and can not be toggled between visible and
          // hidden

    if (getVisible() == show)
      return;

    if (show) {

      if (!parentItem.getVisible())
        return; // parentItem must already be visible

      // create underlying table item and set data in table item to stored
      // data

      Table table = parent.getTable();

      int parentIndex = table.indexOf(parentItem.tableItem);

      int index = parentItem.expandedIndexOf(this) + parentIndex + 1;

      if (index < 0)
        return;

      tableItem = new TableItem(table, getStyle(), index);

      tableItem.setData(this);

      tableItem.setImageIndent(getIndent());

      addCheck();

      // restore fields to item

      // ignore any images in the first column

      int columnCount = Math.max(table.getColumnCount(), 1);

      for (int i = 0; i < columnCount; i++) {

        if (i < texts.length && texts[i] != null)
          setText(i, texts[i]);

        if (i < images.length && images[i] != null)
          setImage(i, images[i]);

      }

      // display the children and the appropriate [+]/[-] symbol as
      // required

      if (items.length != 0) {

        if (expanded) {

          tableItem.setImage(0, parent.getMinusImage());

          for (int i = 0, length = items.length; i < length; i++) {

            items[i].setVisible(true);

          }

        } else {

          tableItem.setImage(0, parent.getPlusImage());

        }

      }

    } else {

      for (int i = 0, length = items.length; i < length; i++) {

        items[i].setVisible(false);

      }

      // remove row from table

      tableItem.dispose();

      tableItem = null;

    }
  }
  

}

