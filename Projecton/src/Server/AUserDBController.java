package Server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Common.ObjectMessage;
import Common.User;

/**
 * This class make the functionality for the server that includes a connection to the DB.
 * The Focus of this class is on functions that deal with 'Users' in general 
 */

public class AUserDBController 
{
	/**
	 * This function sorts the request in the 'msg' to the relevant function and returns the answer
	 * @param msg - the object from the client
	 * @param connToSQL - the connection to the MySQL created in the Class OBLServer
	 * @return ObjectMessage with the answer to the client
	 */
	public static ObjectMessage selection(ObjectMessage msg, Connection connToSQL)
	{
		if((msg.getMessage()).equals("user try to log in"))
		{
			return tryToLogIn(msg, connToSQL);
		}
		else if((msg.getMessage()).equals("user try to log out"))
		{
			return tryToLogOut(msg, connToSQL);
		}
		else if((msg.getMessage()).equals("MakeAllOffline"))
		{
			return LogOutAll(msg, connToSQL);
		}

		return null; 
	}



	/**
	 * This function attempts to commit the log-in for the user and returns "successful" or "unsuccessful" or "not exist"
	 * @param msg
	 * @param connToSQL
	 * @return a message in ObjectMessage
	 */
	private static ObjectMessage tryToLogIn(ObjectMessage msg, Connection connToSQL)
	{
		ObjectMessage answer = new ObjectMessage();

		String permition;
		boolean canContinue = true; //if the user that try to login is 'reader account' and his status is 'Locked' - this flag will be: false

		PreparedStatement getUser = null;  
		PreparedStatement gerReaderAccount = null;  
		PreparedStatement updateOnlineStatus;
		ResultSet rs = null; 
		ResultSet rs2 = null;


		try 
		{
			//get the data of the user from the BD
			getUser = connToSQL.prepareStatement("SELECT * FROM user WHERE ID = ? AND password=? ");
			getUser.setString(1, ((User)msg.getObjectList().get(0)).getId() ); 
			getUser.setString(2, ((User)msg.getObjectList().get(0)).getPassword()); 
			rs =getUser.executeQuery();
						
			if(rs.next())
			{
				
				//if exist and not connected
				if(rs.getString(4).equals("0"))
				{
					
					//check if locked (only if reader account)
					if(3 == rs.getInt(3))
					{
						//get the data of the reader account from the BD
						gerReaderAccount = connToSQL.prepareStatement("SELECT * FROM ReaderAccount WHERE ID = ? ");
						gerReaderAccount.setString(1, ((User)msg.getObjectList().get(0)).getId() );  
						rs2 =gerReaderAccount.executeQuery();

						if(rs2.next())
						{
							//check if locked
							if(rs2.getString(8).equals("Locked"))
							{
								answer.setMessage("unsuccessful");
								answer.setNote("User is Locked");
								canContinue = false;
							}
						}	

					}
					
					if(canContinue) //if it is 'reader account'- he is not 'locked' OR it is 'librarian'/'library director'
					{ 
						//change the user to 'online'
						try 
						{
							updateOnlineStatus = connToSQL.prepareStatement("UPDATE user "+"SET isOnline = ? WHERE ID = ?");
							updateOnlineStatus.setString(1, "1");
							updateOnlineStatus.setString(2, ((User)msg.getObjectList().get(0)).getId());
							updateOnlineStatus.executeUpdate();
						} 
						catch (SQLException e) 
						{
							e.printStackTrace();
						}

						permition = rs.getString(3);
						answer.setNote(permition); 
						answer.setMessage("successful");
					}
									
				}
				else //if exist but already connected
				{
					answer.setMessage("unsuccessful");
					answer.setNote("User already connected");
				}
					
			}
			else //if not exist
			{
				answer.setMessage("not exist");
				answer.setNote("ID or Password not match");
			}

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		return answer;
	}


	/**
	 * This function commits the log out
	 * @param msg
	 * @param connToSQL
	 * @return a message in ObjectMessage
	 */
	private static ObjectMessage tryToLogOut(ObjectMessage msg, Connection connToSQL)
	{
		ObjectMessage answer = new ObjectMessage();

		PreparedStatement updateTime;
		try 
		{
			updateTime = connToSQL.prepareStatement("UPDATE user "+"SET isOnline = ? WHERE ID = ?");
			updateTime.setString(1, "0");
			updateTime.setString(2, ((User)msg.getObjectList().get(0)).getId());
			updateTime.executeUpdate();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

		answer.setMessage("successful log out");
		return answer;
	}


	/**
	 * This function attempts to logout all the users.
	 * @param msg
	 * @param connToSQL
	 * @return a message in ObjectMessage
	 */
	private static ObjectMessage LogOutAll(ObjectMessage msg, Connection connToSQL)
	{
		ObjectMessage answer = new ObjectMessage();

		PreparedStatement updateTime;
		try 
		{
			updateTime = connToSQL.prepareStatement("UPDATE user SET isOnline = 0");
			updateTime.executeUpdate();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		return answer;
	}

}
