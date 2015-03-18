package utils.common

/**
 * Created by liubin on 15-3-16.
 */
object RandomUtils {
  // Random generator
  val random = new scala.util.Random

  // Generate a random string of length n from the given alphabet
  def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

  // Generate a random alphabnumeric string of length n
  def randomAlpString(n: Int) =
    randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)

  def randomDigitString(n: Int) =
    randomString("0123456789")(n)

  def randomHexString(n: Int) =
    randomString("0123456789abcdef")(n)

}
