/**
 * Created by sentipy on 12/07/15.
 */
var app = angular.module('BankApp', ['ui.bootstrap', 'ui.grid']);

app.controller('BankAppCtrl', ['$scope', '$http', '$modal', function ($scope, $http, $modal) {

        $scope.accountsGrid = {
            data: []
            ,enableSorting: true
            , columnDefs: [
                {field: 'accountNumber', name: 'Account Number', width: '40%'
                    , footerCellTemplate: '<div class="ui-grid-cell-contents">Total accounts: {{grid.appScope.accountsGrid.data.length}}</div>'}
                , {field: 'balance', name: 'Balance', width: '40%' }
                , {name: 'Operations', cellTemplate: '<div class="btn-group" dropdown>' +
                        '<button type="button" class="btn btn-sm" style="background-color: lightgray">Choose operation</button>' +
                        '<button type="button" class="btn dropdown-toggle btn-sm" dropdown-toggle>' +
                            '<span class="caret"></span>' +
                            '<span class="sr-only">Split button!</span>' +
                        '</button>' +
                        '<ul class="dropdown-menu" role="menu">' +
                            '<li><a href="#" ng-click="grid.appScope.borrowMoney(row.entity.accountNumber)">Borrow</a></li>' +
                            '<li><a href="#">Transfer</a></li>' +
                            '<li class="divider"></li>' +
                            '<li><a href="#">View documents</a></li>' +
                        '</ul>' +
                '</div>' +
                '<button onclick="alert(1)"></button>', width: '20%'}
            ]
            , showHeader: true
            , showColumnFooter: true
        };
        $scope.accountsLoading = false;

        $scope.openAccount = function() {
            var openAccountInstance = $modal.open({
                animation: true,
                templateUrl: 'angularjsTemplates/openAccount.html',
                controller: 'OpenAccountCtrl',
                backdrop: 'static',
                resolve: {
                    items: function () {
                        return $scope.items;
                    }
                }
            });

            openAccountInstance.result.then(function () {
                $scope.refreshAccounts();
            }, function () {

            });
        };

        $scope.borrowMoney = function(accountNumber) {
            var openAccountInstance = $modal.open({
                animation: true,
                templateUrl: 'angularjsTemplates/borrowMoney.html',
                controller: 'BorrowMoneyCtrl',
                backdrop: 'static',
                resolve: {
                    accountNumber: function() { return accountNumber}
                }
            });

            openAccountInstance.result.then(function () {
                $scope.refreshAccounts();
            }, function () {

            });
        };

        $scope.refreshAccounts = function() {
            $scope.accountsLoading = true;
            $http.post("/api/getAccountsForCurrentClient")
                .success(function(data, status, headers, config) {
                    $scope.accountsLoading = false;
                    $scope.accountsGrid.data = data;
                })
                .error(function(data, status, headers, config) {
                    $scope.accountsLoading = false;
                    alert('Failed to load accounts');
                });
            };
        $scope.refreshAccounts();
        }
    ]
);

app.controller('OpenAccountCtrl', function ($scope, $modalInstance, $http) {

    $scope.ok = function () {
        $http.post("/api/openAccount")
            .success(function(data, status, headers, config) {
                $modalInstance.close();
            })
            .error(function(data, status, headers, config) {
                alert('Failed to open account!');
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

app.controller('BorrowMoneyCtrl', function ($scope, $modalInstance, $http, accountNumber) {

    $scope.accountNumber = accountNumber;
    $scope.sum = 0;
    $scope.errorText = "";

    $scope.ok = function () {
        if (isNaN($scope.sum)) {
            $scope.errorText = "Please set correct number";
            return;
        }
        $http.post("/api/borrow", {accountNumber: $scope.accountNumber, sum: $scope.sum})
            .success(function(data, status, headers, config) {
                $modalInstance.close();
            })
            .error(function(data, status, headers, config) {
                alert('Failed to borrow money!');
            });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});