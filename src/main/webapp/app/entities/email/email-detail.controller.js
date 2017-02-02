(function() {
    'use strict';

    angular
        .module('myappApp')
        .controller('EmailDetailController', EmailDetailController);

    EmailDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Email', 'Person'];

    function EmailDetailController($scope, $rootScope, $stateParams, previousState, entity, Email, Person) {
        var vm = this;

        vm.email = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('myappApp:emailUpdate', function(event, result) {
            vm.email = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
