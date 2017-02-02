(function() {
    'use strict';

    angular
        .module('myappApp')
        .controller('AddressDialogController', AddressDialogController);

    AddressDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Address', 'Person'];

    function AddressDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Address, Person) {
        var vm = this;

        vm.address = entity;
        vm.clear = clear;
        vm.save = save;
        vm.people = Person.query({filter: 'address-is-null'});
        $q.all([vm.address.$promise, vm.people.$promise]).then(function() {
            if (!vm.address.person || !vm.address.person.id) {
                return $q.reject();
            }
            return Person.get({id : vm.address.person.id}).$promise;
        }).then(function(person) {
            vm.people.push(person);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.address.id !== null) {
                Address.update(vm.address, onSaveSuccess, onSaveError);
            } else {
                Address.save(vm.address, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('myappApp:addressUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
