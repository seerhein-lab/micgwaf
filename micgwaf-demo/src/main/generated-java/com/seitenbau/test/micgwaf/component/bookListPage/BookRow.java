package com.seitenbau.test.micgwaf.component.bookListPage;

import com.seitenbau.micgwaf.component.Component;
import com.seitenbau.test.micgwaf.service.Book;

/**
 * This class represents the HTML element with m:id bookRow.
 * Instances of this class are used whenever these elements are rendered
 * or when form date from a page containing these elements is processed.
 **/
public class BookRow extends BaseBookRow
{
  /** Serial Version UID. */
  private static final long serialVersionUID = 1L;

  public Integer bookId;

  /**
  * Constructor. 
  *
  * @param parent the parent component, or null if this is a standalone component (e.g. a page)
  */
  public BookRow(Component parent)  {
    super(parent);
  }
  

  public BookRow(Component parent, Book book)
  {
    super(parent);
    display(book);
    hideErrorBoxes();
  }

  public void display(Book book)
  {
    // TODO simplify mapping ?
    author.setTextContent(book.getAuthor());
    title.setTextContent(book.getTitle());
    publisher.setTextContent(book.getPublisher());
    isbn.setTextContent(book.getIsbn());
    bookId = book.getId();
  }
  
  public Book getDisplayedBook()
  {
    // TODO simplify mapping ?
    Book book = new Book(bookId);
    book.setAuthor(author.getTextContent());
    book.setTitle(title.getTextContent());
    book.setPublisher(publisher.getTextContent());
    book.setIsbn(isbn.getTextContent());
    return book;
  }

  public Book getEditedBook()
  {
    // TODO simplify mapping ?
    Book book = new Book(bookId);
    book.setAuthor(authorErrorBox.authorInput.submittedValue);
    book.setTitle(titleErrorBox.titleInput.submittedValue);
    book.setPublisher(publisherErrorBox.publisherInput.submittedValue);
    book.setIsbn(isbnErrorBox.isbnInput.submittedValue);
    return book;
  }

  public void editMode()
  {
    author.setRender(false);
    title.setRender(false);
    publisher.setRender(false);
    isbn.setRender(false);
    authorErrorBox.renderChildren = true;
    authorErrorBox.authorInput.setRender(true);
    authorErrorBox.authorInput.setValue(author.getTextContent());
    titleErrorBox.renderChildren = true;
    titleErrorBox.titleInput.setRender(true);
    titleErrorBox.titleInput.setValue(title.getTextContent());
    publisherErrorBox.renderChildren = true;
    publisherErrorBox.publisherInput.setRender(true);
    publisherErrorBox.publisherInput.setValue(publisher.getTextContent());
    isbnErrorBox.renderChildren = true;
    isbnErrorBox.isbnInput.setRender(true);
    isbnErrorBox.isbnInput.setValue(isbn.getTextContent());
    saveButton.setRender(true);
    cancelEditButton.setRender(true);
    editInlineButton.setRender(false);
    editExtraPageButton.setRender(false);
    editInlineAjaxButton.setRender(false);
  }
  
  public void displayMode()
  {
    author.setRender(true);
    title.setRender(true);
    publisher.setRender(true);
    isbn.setRender(true);
    authorErrorBox.setRender(false);
    titleErrorBox.setRender(false);
    publisherErrorBox.setRender(false);
    isbnErrorBox.setRender(false);
    saveButton.setRender(false);
    cancelEditButton.setRender(false);
    editInlineButton.setRender(true);
    editExtraPageButton.setRender(true);
    editInlineAjaxButton.setRender(true);
  }
  
  public boolean isInDisplayMode()
  {
    return editInlineButton.renderSelf;
  }

  public void hideErrorBoxes()
  {
    authorErrorBox.renderSelf = false;
    titleErrorBox.renderSelf = false;
    publisherErrorBox.renderSelf = false;
    isbnErrorBox.renderSelf = false;
  }
}
