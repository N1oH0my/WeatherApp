# Приложение о погоде

-Приложение для прогнозирования погоды, которое позволяет легко получать информацию о текущих и будущих погодных условиях с помощью weatherapi от accuweather
-Получите доступ к прогнозу погоды на 5 дней и 12 часов, чтобы планировать заранее.
- Получайте обновления погоды для различных городов, что позволит вам быть в курсе погодных условий по всему миру.

</br>
Написано на Kotlin

## Описание

* При входе в приложение проверяется, включен ли GPS, и запрашивается соответствующее разрешение
* Изначально и при обновлении нажатием на соответствующую кнопку всегда отображается прогноз погоды для текущего местоположения пользователя
* Интерфейс содержит описание погоды и сопровождается соответствующей картинкой
* Прогноз погоды на день характеризуется максимальными и минимальными температурами
* Для просмотра прогноза погоды в нем можно выбрать любой другой город мира

<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/1.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/2.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/3.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/4.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/5.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/6.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/7.jpg" alt="Alt текст">
<img src="https://github.com/N1oH0my/CocktailBar/blob/master/img/8.jpg" alt="Alt текст">

<li>Данные хранятся в "MutableLiveData MutableList(Cocktail)" с подпиской на изменения.</li>
<li>Для сохранения данных используется SharedPreferences.</li>
<li>Также используется binding для упрощения работы с xml.</li>
