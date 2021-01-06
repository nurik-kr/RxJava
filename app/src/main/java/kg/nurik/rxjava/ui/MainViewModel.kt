package kg.nurik.rxjava.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kg.nurik.rxjava.data.model.Search
import kg.nurik.rxjava.data.model.SearchModel
import kg.nurik.rxjava.data.remote.RetrofitInterface
import java.util.concurrent.TimeUnit

class MainViewModel(private val network: RetrofitInterface) : ViewModel() {

    val progress = MutableLiveData<Boolean>()
    private var disposable: Disposable? = null // хранилище где храниться один поток
    private var compositeDisposable = CompositeDisposable() //collecton zaprosov
    private val filmBehaviorSubject = BehaviorSubject.create<String>() // задержка
    val data = MutableLiveData<SearchModel>()

    init {
        compositeDisposable.add(
            filmBehaviorSubject
                .debounce(2000, TimeUnit.MILLISECONDS) //задержка в 2сек
                .subscribe({
                    searchFilm(it)
                }, {
                    Log.d("____ADssadsad", "Error")
                })
        )
       // searchRepeatWhen()
    }

    fun search(text: String) {
        filmBehaviorSubject.onNext(text)
    }

    fun searchRepeatWhen() { //каждые 5сек будем повторять этот запрос
        compositeDisposable.add(
            network.searchFilm2("4f391043", "dark")
                .subscribeOn(Schedulers.io())                           //поток
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen {
                    it.delay(5, TimeUnit.SECONDS)          // .take(10) //take count, 11 stop
                }
//                .takeUntil{
//                    return@takeUntil it.search != null //до тех пор пока наш список пустой , до того момента как в наш список придет значение
//                }
                .subscribe({
                    data.postValue(it)
                }, {
                    Log.d("repeatWhen", "Error")
                })
        )
    }

    private fun concat(text: String) { //для нескольких операций(они должны быть очень близки)
        val res = network.searchFilm2("4f391043", text)
        val res2 = network.searchFilm2("4f391043", text)
        compositeDisposable.add(
            Observable.concat(res, res2)
                .first(
                    SearchModel( //если 1запрос отработает то вернется res в subscribe , а 2ой пропуститься
                        response = "",
                        search = listOf(),
                        totalResults = ""
                    )
                ) //если оба запроса отвалятся вернется это дефолт значение
                .subscribe({

                }, {

                })
        )
    }

    private fun zip(text: String) {
        val res = network.searchFilm2("4f391043", text)
        val res2 = network.searchFilm2("4f391043", text)
        compositeDisposable.add( //можем обьединить результат двух запросов в один
            Observable.zip(
                res, res2,
                BiFunction<SearchModel, SearchModel, SearchModel> { t1, t2 ->
                    val list = arrayListOf<Search>()
                    t1.search?.let { list.addAll(it) }
                    t2.search?.let { list.addAll(it) }
                    SearchModel(
                        totalResults = t1.totalResults,
                        search = list,
                        response = t1.response
                    )
                }
            )
                .subscribe({

                }, {

                })
        )
    }

    private fun searchFilm(text: String) { //нужно указать в каком потоке будет запрос и в каком потоке будет отдаваться данные
//        disposable = 1вариант
        compositeDisposable.add(network.searchFilm("4f391043", text)
            .subscribeOn(Schedulers.io())               //поток где будет выпол запрос - это бэкраунд поток
            .observeOn(AndroidSchedulers.mainThread())            //эта поток в котором будет отдаваться результаты,в маин потоке
            .doOnSubscribe {
                progress.postValue(true)
            }       //что мы еще хотим делать во время этого запроса , должны указать какой то прогресс
            .doFinally {
                progress.postValue(false)
            } //тот момент когда запрос закончил свою работу
//            .map {
//                val result = it.search //можем приоброзовать модельку в другую
//                return@map result
//            }
//            .map {
//                val data = it.map { //можем поменять модельку в другую и каждый по очереди выполниться
//                        Search2(
//                            poster = it.poster,
//                            title = "Nurik",
//                            type = it.type,
//                            year = it.year,
//                            imdbID = it.imdbID
//                        )}
//                return@map data
//            }
//            .flatMap {
//                return@flatMap network.searchFilm("4f391043", text) //второй запрос сразу же
//            }
//            .filter {
//                return@filter it.totalResults.toInt() > 0 //получается фильтруем totalResults если он больше то мы попадаем сюда
//            }
            .retryWhen { throwable ->
                throwable.take(3).delay(1, TimeUnit.SECONDS)
            } //если ошибка,то запрос будет выполняться 3раза с задержкой 1сек
            .subscribe({
                data.postValue(it)
                Log.d("____ADssadsad", "success")
            }, {
                Log.d("____ADssadsad", "success")
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose() //уничтоважется запросы которые находятся в том экрана
        compositeDisposable.dispose() //второй вариант
    }
}