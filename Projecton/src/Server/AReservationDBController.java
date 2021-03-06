package Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import Common.Book;
import Common.Copy;
import Common.History;
import Common.IEntity;
import Common.Mail;
import Common.ObjectMessage;
import Common.ReaderAccount;
import Common.Reservation;

/**
 * This class make the functionality for the server that includes a connection to the DB.
 * The Focus of this class is on functions that deal with 'Reservations'
 */

public abstract class AReservationDBController 
{

	/**
	 * This function sorts the request in the 'msg' to the relevant function and returns the answer
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	public static ObjectMessage selection(ObjectMessage msg, Connection connToSQL)
	{

		if (((msg.getMessage()).equals("get reserves")))
		{
			return getReserves(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("reserveBook")))
		{
			return reserveBook(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("implement reservation")))
		{
			return implementReserveBook(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("cancel reservation")))
		{
			return cancelReservation(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("letImplementReservation")))
		{
			return letImplementReservation(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("getReaderThatCanImplement")))
		{
			return getReaderThatCanImplement(msg, connToSQL);
		}
		else if (((msg.getMessage()).equals("deleteAllReservations")))
		{
			return deleteAllReservations(msg, connToSQL);
		}
		else
		{ 
			return null; 
		}
	}

	/**
	 * this method implement reservation it recieve reader account and reservation 
	 * it check if we can to borrow the copy and consider the queue of reservation for each book
	 * the practical borrow progress in the method implementTheBorrow
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage implementReserveBook(ObjectMessage msg, Connection connToSQL) 
	{
		ObjectMessage answer = new ObjectMessage();  
		ReaderAccount reader=(ReaderAccount) msg.getObjectList().get(0);
		Reservation reserve=(Reservation) msg.getObjectList().get(1);

		PreparedStatement isActive = null;
		PreparedStatement numOfCopyAvailable = null;
		PreparedStatement whoIstheFirst=null;
		PreparedStatement numOfCopy=null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		ResultSet temp1 = null;
		int copyAvailable, numOfBookReserve;
		String temp;
		try
		{ 

			isActive = connToSQL.prepareStatement("SELECT * FROM readeraccount WHERE ID = ? ");
			isActive.setString(1, reader.getId()); 
			rs1 =isActive.executeQuery();
			rs1.next();
			String status=rs1.getString(8);
			if(!(status.equals("Active")))
			{
				answer.setMessage("ReservationNotImplemented");
				answer.setNote("The reader account is not active");
				return answer;
			}
			else
			{
				numOfCopyAvailable = connToSQL.prepareStatement("SELECT COUNT(*) FROM copy WHERE bookId=? AND borrowerId is null"); 
				numOfCopyAvailable.setInt(1, reserve.getBookID());
				rs2 =numOfCopyAvailable.executeQuery();
				rs2.next();
				copyAvailable=rs2.getInt(1);
				if(copyAvailable==0)
				{
					answer.setMessage("ReservationNotImplemented");
					answer.setNote("No copy available");
					return answer;
				}
				else
				{
					numOfCopyAvailable = connToSQL.prepareStatement("SELECT COUNT(*) FROM reservations WHERE bookId=?"); 
					numOfCopyAvailable.setInt(1, reserve.getBookID());
					rs3 =numOfCopyAvailable.executeQuery();
					rs3.next();
					numOfBookReserve=rs3.getInt(1);
					if(copyAvailable>=numOfBookReserve)
					{
						answer=implementTheBorrow(msg,connToSQL);
					}
					else
					{	
						whoIstheFirst = connToSQL.prepareStatement("SELECT * FROM reservations WHERE bookId=? order by Date"); 
						whoIstheFirst.setInt(1, reserve.getBookID());
						rs4 =whoIstheFirst.executeQuery();
						rs4.next();
						Date date=rs4.getDate(3);
						rs4.beforeFirst();
						while(rs4.next())
						{
							temp=rs4.getString(2);
							if(reader.getId().equals(temp) && (date.equals(rs4.getDate(3))))//the first in the queue
							{
								answer=implementTheBorrow(msg,connToSQL);
								return answer;
							}

						}
						answer.setMessage("ReservationNotImplemented");
						answer.setNote("The reader account is not the first on queue");
						answer.addObject(msg.getObjectList().get(0));
					}
				}
			}

		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
		return answer;
	}

	/**
	 * This function makes the borrow implementation
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage implementTheBorrow(ObjectMessage msg, Connection connToSQL) 
	{
		ObjectMessage answer = new ObjectMessage();
		ReaderAccount reader=(ReaderAccount) msg.getObjectList().get(0);
		Reservation reserve=(Reservation) msg.getObjectList().get(1);
		PreparedStatement getBook,numOfCopy;
		ResultSet rs1=null;
		ResultSet rs2=null;
		try
		{
			getBook = connToSQL.prepareStatement("SELECT * FROM Book WHERE bookId = ? ");
			getBook.setInt(1, reserve.getBookID());
			rs1 = getBook.executeQuery();
			rs1.next();
			numOfCopy = connToSQL.prepareStatement("UPDATE `copy` SET `borrowerId`=?, `borrowDate`=?,`returnDate`=? WHERE bookId=? AND borrowerId is null"); 
			numOfCopy.setString(1, reader.getId());

			if(rs1.getBoolean(6))
			{
				LocalDate now = LocalDate.now();
				Date today=java.sql.Date.valueOf(now);
				LocalDate nowPlus3 = LocalDate.now().plusDays(3);
				Date nowPlus3Date = java.sql.Date.valueOf(nowPlus3);

				numOfCopy.setDate(2, (java.sql.Date) today);
				numOfCopy.setDate(3, (java.sql.Date) nowPlus3Date);
			}
			else 
			{
				LocalDate now = LocalDate.now();
				Date today=java.sql.Date.valueOf(now);
				LocalDate nowPlus14 = LocalDate.now().plusDays(14);
				Date nowPlus14Date = java.sql.Date.valueOf(nowPlus14);

				numOfCopy.setDate(2, (java.sql.Date) today);
				numOfCopy.setDate(3, (java.sql.Date) nowPlus14Date);
			}

			numOfCopy.setInt(4, reserve.getBookID());
			numOfCopy.executeUpdate();

			answer.setMessage("ReservationImplemented");
			PreparedStatement deleteReserve = connToSQL.prepareStatement("delete FROM reservations WHERE bookId=? AND readerAccountID=?");
			deleteReserve.setInt(1, reserve.getBookID());
			deleteReserve.setString(2, reader.getId());
			deleteReserve.executeUpdate();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		//get number of copy for History table
		PreparedStatement getBookId;
		try {
			getBookId = connToSQL.prepareStatement("SELECT * FROM Copy WHERE bookId = ? AND borrowerId =?");
			getBookId.setInt(1, reserve.getBookID());
			getBookId.setString(2,reader.getId());
			rs2 = getBookId.executeQuery();
			rs2.next();

			//add `implement reservation book` to HISTORY
			LocalDate now = LocalDate.now(); 
			Date today = java.sql.Date.valueOf(now);
			History sendObject =new History( reader.getId(),"Borrow book",reserve.getBookID(),rs2.getInt(1),(java.sql.Date) today);
			AHistoryDBController.enterActionToHistory(sendObject, connToSQL);
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return answer;
	}

	/**
	 * This function returns the list of reservation of the reader account
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage getReserves(ObjectMessage msg, Connection connToSQL) 
	{
		ObjectMessage answer = null;  
		ReaderAccount reader=(ReaderAccount) msg.getObjectList().get(0);
		boolean resultExist = false;

		PreparedStatement getReserves = null;  
		PreparedStatement getBook = null;
		ResultSet rs1 = null; 
		ResultSet rs2 = null; 

		try 
		{
			//get the copies that the reader account borrowed 
			getReserves = connToSQL.prepareStatement("SELECT * FROM Reservations WHERE readerAccountID = ? ");
			getReserves.setString(1, reader.getId() ); 
			rs1 =getReserves.executeQuery();

			ArrayList <IEntity> result=new ArrayList<IEntity>(); 

			//go by all the reservations that the reader account reserved and get the book of each one
			while(rs1.next())
			{
				resultExist = true;

				int bookId = rs1.getInt(1); //the bookID of the current copy
				String date = rs1.getString(3);

				//get the book of that reserve
				getBook = connToSQL.prepareStatement("SELECT * FROM Book WHERE bookId = ? ");
				getBook.setInt(1, bookId ); 
				rs2 =getBook.executeQuery();
				if(rs2.next())
				{					
					//set the reservation info from the both queries
					Reservation reservation = new Reservation(bookId, date, rs2.getString(2), rs2.getString(3), rs2.getString(4),rs2.getString(5), rs2.getString(6), rs2.getString(7)); 
					result.add(reservation);  
				}

			}

			if(resultExist)
			{
				answer = new ObjectMessage(result,"TheReserves"," ");
			}
			else
			{
				answer = new ObjectMessage(result,"NoReserves"," ");
			}

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}


		return answer;
	}

	/**
	 * This function let readerAccount reserve a book if doesnt have reserve for book 
	 * also check that all the copies of the book is borrowed and the readerAccount is active
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage reserveBook(ObjectMessage msg, Connection connToSQL)
	{
		PreparedStatement ps;
		ObjectMessage answer;
		ResultSet rs;
		Book askedBook=(Book)msg.getObjectList().get(0);
		ReaderAccount askedReaderAccount=(ReaderAccount)msg.getObjectList().get(1);
		Copy copy=new Copy(-1,askedBook.getBookID(),null);
		ObjectMessage message=new ObjectMessage(copy,"checkIfAllBorrowed","Copy");
		ObjectMessage resultOfCopy=ACopyDBController.selection(message,connToSQL);
		if(resultOfCopy.getNote().equals("FoundBook"))
		{
			answer=new ObjectMessage("reserveBook","HaveAvailableCopy");
			return answer;
		}
		else
		{
			message=new ObjectMessage();
			message.addObject(msg.getObjectList().get(0), msg.getObjectList().get(1));
			ObjectMessage resultOfExistReserve=AReservationDBController.alreadyReserveBook(message,connToSQL);
			if(resultOfExistReserve.getNote().equals("FoundReserve"))
			{
				answer=new ObjectMessage("reserveBook","ExistReserve");
				return answer;
			}
			else
			{
				message=new ObjectMessage("checkIfUserGotAlreadyCopy","Copy");
				message.addObject(msg.getObjectList().get(0), msg.getObjectList().get(1));
				ObjectMessage resultIfUserGotAlreadyCopy=ACopyDBController.selection(message,connToSQL);
				if(resultIfUserGotAlreadyCopy.getNote().equals("HaveCopy"))
				{
					answer=new ObjectMessage("reserveBook","HaveCopy");
					return answer;
				}
				else
				{
					try 
					{
						ps = connToSQL.prepareStatement("SELECT status FROM obl.readeraccount WHERE ID=?");
						ps.setString(1,askedReaderAccount.getId());
						rs=ps.executeQuery();
						rs.next();
						if(rs.getString(1).equals("Active"))
						{
							Date date = new Date();
							Date time= new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
							String currentDate = sdf.format(date);
							String currentTime = sdf2.format(date);
							try 
							{
								date=new SimpleDateFormat("yyyy-MM-dd").parse(currentDate);
								time=new SimpleDateFormat("hh:mm:ss").parse(currentTime);
							} 
							catch (ParseException e) 
							{

								e.printStackTrace();
							}
							java.sql.Date sqlDate = new java.sql.Date(date.getTime()); 
							java.sql.Time sqlTime = new java.sql.Time(time.getTime()); 
							ps = connToSQL.prepareStatement("INSERT INTO `Reservations` (`bookId`,`readerAccountID`, `Date`,`Time`) VALUES (?,?,?,?)");
							ps.setInt(1,askedBook.getBookID());
							ps.setString(2,askedReaderAccount.getId());
							ps.setDate(3, sqlDate);
							ps.setTime(4, sqlTime);
							ps.executeUpdate();


							//add `reservation book` to HISTORY
							LocalDate now = LocalDate.now(); 
							Date today = java.sql.Date.valueOf(now);
							History sendObject =new History(askedReaderAccount.getId(),"Reserve book",askedBook.getBookID(),(java.sql.Date) today);
							AHistoryDBController.enterActionToHistory(sendObject, connToSQL);

							answer=new ObjectMessage("reserveBook","Reserved");

							return answer;

						}
						else
						{
							answer=new ObjectMessage("reserveBook","The Status of reader account is "+rs.getString(1)+"\nHe can't reserve book.");
							return answer;
						}
					} 
					catch (SQLException e) 
					{
						e.printStackTrace();
						return null;
					}
				}
			}	
		}

	}

	/**
	 * This function check if reader account doesnt have reserve for book 
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage alreadyReserveBook(ObjectMessage msg, Connection connToSQL)
	{
		PreparedStatement ps;
		ObjectMessage answer;
		Book askedBook=(Book)msg.getObjectList().get(0);
		ReaderAccount askedReaderAccount=(ReaderAccount)msg.getObjectList().get(1);
		try 
		{
			ps = connToSQL.prepareStatement("SELECT COUNT(*) FROM obl.Reservations WHERE bookId=? AND readerAccountID=?");
			ps.setInt(1,askedBook.getBookID());
			ps.setString(2,askedReaderAccount.getId());
			ResultSet rs = ps.executeQuery();
			rs.next();
			int x =rs.getInt(1);
			if(rs.getInt(1)!= 0)
			{
				return new ObjectMessage("alreadyReserveBook","FoundReserve");
			}
			else
			{
				return new ObjectMessage("alreadyReserveBook","NoFoundReserve");
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This function cancel reservation for spesific book and spesific reader account
	 * if have somone next in the queue let the next person implement
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage cancelReservation(ObjectMessage msg, Connection connToSQL)
	{
		PreparedStatement ps;
		ReaderAccount askedReaderAccount=(ReaderAccount)msg.getObjectList().get(0);
		Reservation reservation=(Reservation)msg.getObjectList().get(1);
		try 
		{
			ps = connToSQL.prepareStatement("DELETE FROM `Reservations` WHERE bookId= ? AND readerAccountID= ?");
			ps.setInt(1,reservation.getBookID());
			ps.setString(2,askedReaderAccount.getId());
			ps.executeUpdate();
			Book askedbook=new Book();
			askedbook.setBookID(reservation.getBookID());
			ObjectMessage askTheFirstReader=new ObjectMessage();
			askTheFirstReader.addObject(askedbook);
			ObjectMessage result=getReaderThatCanImplement(askTheFirstReader,connToSQL);
			if(result.getNote().equals("Found"))
			{
				askTheFirstReader=new ObjectMessage(result.getObjectList().get(0),"SearchReader","ReaderAccount");
				ReaderAccount readerAccount =(ReaderAccount) (AReaderAccountDBController.selection(askTheFirstReader, connToSQL)).getObjectList().get(0);
				ObjectMessage bookDetails=new ObjectMessage(askedbook,"searchBookID","Book");
				Book book = (Book) (ABookDBController.selection(bookDetails, connToSQL)).getObjectList().get(0);
				ObjectMessage implementReservation=new ObjectMessage();
				implementReservation.addObject(readerAccount, book);
				letImplementReservation(implementReservation,connToSQL);
			}

			//add `cancel reservation book` to HISTORY
			LocalDate now = LocalDate.now(); 
			Date today = java.sql.Date.valueOf(now);
			History sendObject =new History(askedReaderAccount.getId(),"Cancel reservation book",reservation.getBookID(),(java.sql.Date) today);
			AHistoryDBController.enterActionToHistory(sendObject, connToSQL);

			return new ObjectMessage("ReservationCanceled","cancelReservation");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * This function let the next readerAccount implement his resrvation 
	 * start the time of 48 hours and notify him about it
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	private static ObjectMessage letImplementReservation(ObjectMessage msg, Connection connToSQL)
	{

		PreparedStatement ps;
		ReaderAccount askedReaderAccount=(ReaderAccount)msg.getObjectList().get(0);
		Book book=(Book)msg.getObjectList().get(1);
		try 
		{
			ps = connToSQL.prepareStatement("UPDATE `reservations` SET `startTimerImplement`=NOW() WHERE bookId= ? AND readerAccountID= ? ");
			ps.setInt(1,book.getBookID());
			ps.setString(2,askedReaderAccount.getId());
			ps.executeUpdate();


			//send mail
			ObjectMessage notify=new ObjectMessage("sendMail","Daily");
			Mail mail=new Mail();
			mail.setTo(askedReaderAccount.getEmail());
			String body="Hello "+askedReaderAccount.getFirstName()+"\nWe glad to notfiy you that you can come to library"
					+ " and implement your reservation for "+book.getBookName()
					+ ".\nTake care , you got only 48 hours to implement reservation since the time of this mail."
					+"\n 		Thank you , Ort Braude Library";
			mail.setBody(body);
			String subject="Implement your reservation for "+book.getBookName();
			mail.setSubject(subject);
			notify.addObject(mail);
			ADailyDBController.selection(notify, connToSQL);

		} 
		catch (SQLException e) 
		{

			e.printStackTrace();
			return null;
		} 

		return new ObjectMessage("letImplementReservation","SentMail");
	}

	/**
	 * This function check if have somone that can implement reservation for book 
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client , if found reader account send him else send propper message
	 */
	private static ObjectMessage getReaderThatCanImplement(ObjectMessage msg, Connection connToSQL)
	{
		PreparedStatement ps;
		Book book=(Book)msg.getObjectList().get(0); 
		ResultSet rs = null; 

		//get the copies that the reader account borrowed 
		try 
		{
			ps = connToSQL.prepareStatement("SELECT COUNT(*) FROM reservations WHERE bookId=? AND startTimerImplement IS NULL");
			ps.setInt(1, book.getBookID()); 
			rs =ps.executeQuery();
			rs.next();
			if(rs.getInt(1)==0)
			{
				return new ObjectMessage("ReaderThatCanImplement","NoFound");
			}
			else
			{
				ps = connToSQL.prepareStatement("SELECT * FROM Reservations WHERE bookId = ? AND startTimerImplement IS NULL ORDER BY `Date`,`Time` ");
				ps.setInt(1, book.getBookID()); 
				rs =ps.executeQuery();
				rs.next();
				ReaderAccount readerAccount=new ReaderAccount();
				readerAccount.setId(rs.getString(2));
				return new ObjectMessage(readerAccount,"ReaderThatCanImplement","Found");
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return new ObjectMessage("ReaderThatCanImplement","NoFound");
		}
	}

	/**
	 * This function triggerd when somone lost his book and it was the last copy of this book
	 * the function delete all the reservations for book and notify all the people who had reservation
	 * for this book.
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client 
	 */
	private static ObjectMessage deleteAllReservations(ObjectMessage msg, Connection connToSQL)
	{
		PreparedStatement ps;
		Book book=(Book)msg.getObjectList().get(0); 
		ResultSet rs;
		//delete all reservations
		try 
		{
			ps = connToSQL.prepareStatement("SELECT COUNT(*)  FROM obl.reservations WHERE bookId=?");
			ps.setInt(1,book.getBookID());
			rs=ps.executeQuery();
			rs.next();
			if(rs.getInt(1)!=0)
			{
				ps = connToSQL.prepareStatement("SELECT readerAccountID  FROM obl.reservations WHERE bookId=?");
				ps.setInt(1,book.getBookID());
				rs=ps.executeQuery();
				while(rs.next())
				{
					ReaderAccount getReaderAccount=new ReaderAccount();
					getReaderAccount.setId(rs.getString(1));
					ObjectMessage askReaderAccount=new ObjectMessage(getReaderAccount,"SearchReader","ReaderAccount");
					ReaderAccount readerAccount =(ReaderAccount) (AReaderAccountDBController.selection(askReaderAccount, connToSQL)).getObjectList().get(0);

					ObjectMessage askBookDetails=new ObjectMessage(book,"searchBookID","Book");
					Book bookDetails = (Book) (ABookDBController.selection(askBookDetails, connToSQL)).getObjectList().get(0);

					ObjectMessage notify=new ObjectMessage("sendMail","Daily");
					Mail mail=new Mail();
					mail.setTo(readerAccount.getEmail());
					String body="Hello "+readerAccount.getFirstName()+"\nWe sorry to tell you but the book "+bookDetails.getBookName()
					+ " lost.\n its was our last copy of this book."
					+ ".\nSo we want to notify you that your reservation have been canceled."
					+"\n 		Thank you , Ort Braude Library";
					mail.setBody(body);
					String subject="Canceled your reservation for "+bookDetails.getBookName();
					mail.setSubject(subject);
					notify.addObject(mail);
					ADailyDBController.selection(notify, connToSQL);
				}
				ps = connToSQL.prepareStatement("DELETE FROM obl.reservations WHERE bookId=?");
				ps.setInt(1,book.getBookID());
				ps.executeUpdate();

			}

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return null;
	}



}
