import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import scala.collection.Map

class ProductsSoldByLocation(sc: SparkContext) {

    def run(t: String, u: String) : RDD[(String,String)] = {

      val transactions = sc.textFile(t)
      val newTransactionsPair = transactions.map { t =>
      val p = t.split(",")
      (p(2).toInt, p(1).toInt)
    }

      val users = sc.textFile(u)
      val newUsersPair = users.map { t =>
      val p = t.split(",")
      (p(0).toInt, p(3))
    }

    val result = processData(newTransactionsPair, newUsersPair)
      return sc.parallelize(result.toSeq).map(t => (t._1.toString, t._2.toString))
}

  def processData (t: RDD[(Int, Int)], u: RDD[(Int, String)]) : Map[Int,Long] = {
      var jn = t.leftOuterJoin(u).values.distinct
      return jn.countByKey
  }

}


object NumberOfProductsSoldByLocation {
  def main(args: Array[String]) {
        val transactionsIn = args(1)
        val usersIn = args(0)
        val conf = new SparkConf().setAppName("SparkJoins").setMaster("local")
        val context = new SparkContext(conf)
        val job = new ProductsSoldByLocation(context)
        val results = job.run(transactionsIn, usersIn)
        val output = args(2)
        results.saveAsTextFile(output)
        context.stop()
  }
}
