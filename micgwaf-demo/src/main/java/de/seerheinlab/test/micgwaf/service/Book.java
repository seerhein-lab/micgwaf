package de.seerheinlab.test.micgwaf.service;


/**
 * Business-Object for a Book.
 */
public class Book
{
  /** Primary key. */
  private int id;
  
  /** The book title. */
  private String title;
  
  /** The book's author (s). */
  private String author;

  /** The International Standard Book Number. */
  private String isbn;
  
  /** The publisher. */
  private String publisher;

  /**
   * Standard constructor.
   */
  public Book(int id)
  {
    this.id = id;
  }
  
  /**
   * Copy constructor.
   * 
   * @param toCopy the book to copy, not null.
   * @param newId the new id of the copied book.
   */
  public Book(Book toCopy, int newId)
  {
    this.id = newId;
    this.title = toCopy.title;
    this.author = toCopy.author;
    this.publisher = toCopy.publisher;
    this.isbn = toCopy.isbn;
  }
  
  public int getId()
  {
    return id;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getAuthor()
  {
    return author;
  }

  public void setAuthor(String author)
  {
    this.author = author;
  }

  public String getPublisher()
  {
    return publisher;
  }

  public void setPublisher(String publisher)
  {
    this.publisher = publisher;
  }
  
  public String getIsbn()
  {
    return isbn;
  }

  public void setIsbn(String isbn)
  {
    this.isbn = isbn;
  }

  @Override
  public String toString()
  {
    return "[Id=" + id
        + " Title=" + title
        + " Author=" + author
        + " Publisher=" + publisher
        + " isbn=" + isbn
        + "]";
    
  }
}
